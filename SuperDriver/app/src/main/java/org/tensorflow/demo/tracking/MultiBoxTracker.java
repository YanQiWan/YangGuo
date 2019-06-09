/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.tensorflow.demo.tracking;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.widget.Toast;

import com.example.application.SysApplication;
import com.example.superdriver.R;

import org.tensorflow.demo.Classifier.Recognition;
import org.tensorflow.demo.env.BorderedText;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * A tracker wrapping ObjectTracker that also handles non-max suppression and matching existing
 * objects to new detections.
 */
public class MultiBoxTracker {
    private final Logger logger = new Logger();

    private static final float TEXT_SIZE_DIP = 18;

    // Maximum percentage of a box that can be overlapped by another box at detection time. Otherwise
    // the lower scored box (new or old) will be removed.
    private static final float MAX_OVERLAP = 0.2f;

    private static final float MIN_SIZE = 16.0f;

    // Allow replacement of the tracked box with new results if
    // correlation has dropped below this level.
    private static final float MARGINAL_CORRELATION = 0.75f;

    // Consider object to be lost if correlation falls below this threshold.
    private static final float MIN_CORRELATION = 0.3f;

    private static final int[] COLORS = {
            Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.WHITE,
            Color.parseColor("#55FF55"), Color.parseColor("#FFA500"), Color.parseColor("#FF8888"),
            Color.parseColor("#AAAAFF"), Color.parseColor("#FFFFAA"), Color.parseColor("#55AAAA"),
            Color.parseColor("#AA33AA"), Color.parseColor("#0D0068")
    };

    private final Queue<Integer> availableColors = new LinkedList<Integer>();

    public ObjectTracker objectTracker;

    final List<Pair<Float, RectF>> screenRects = new LinkedList<Pair<Float, RectF>>();

    //报警模块
    private MediaPlayer mediaPlayer = null;
    private float x_p = 1.35f;
    private float y_p = 0.6f;
    private float left_x_a = x_p * SysApplication.Screen_px_width;
    private float left_y_b = y_p * SysApplication.Screen_px_height;
    private float left_end_x = (1 - 0.5f / y_p) * left_x_a;
    private float left_end_y = 0.5f * SysApplication.Screen_px_height;
    private float left_start_x = 0;
    private float left_start_y = SysApplication.Screen_px_height * y_p;
    private float right_y_b = -SysApplication.Screen_px_height * y_p * (1 - x_p) / x_p;
    private float right_x_a = -SysApplication.Screen_px_width * (x_p - 1);
    private float right_start_x = SysApplication.Screen_px_width;
    private float right_start_y = SysApplication.Screen_px_height * y_p;
    private float right_end_y = 0.5f * SysApplication.Screen_px_height;
    private float right_end_x = (1 - 0.5f * SysApplication.Screen_px_height / right_y_b) * right_x_a;
    private float intersection_y = right_y_b * left_y_b * (right_x_a - left_x_a) / (right_x_a * left_y_b - right_y_b * left_x_a);
    private float intersection_x = right_x_a * left_x_a * (left_y_b - right_y_b) / (right_x_a * left_y_b - right_y_b * left_x_a);
    //  private float intersection_y = 0.3f * SysApplication.Screen_px_height;
    // private float intersection_x = 0.5f * SysApplication.Screen_px_width;

    private float left_end_x2 = SysApplication.Screen_px_width;
    private float left_end_y2 = (1 - SysApplication.Screen_px_width / left_x_a) * left_y_b;
    private float right_end_x2 = 0;
    private float right_end_y2 = right_y_b;
    private PriorityQueue<AlarmEvent> priorityQueue = null;

    private Map<Integer, Integer> event2rawId = new HashMap<>();

    private static class TrackedRecognition {
        ObjectTracker.TrackedObject trackedObject;
        RectF location;
        float detectionConfidence;
        int color;
        String title;
    }

    private final List<TrackedRecognition> trackedObjects = new LinkedList<TrackedRecognition>();

    private final Paint boxPaint = new Paint();

    private final float textSizePx;
    private final BorderedText borderedText;

    private Matrix frameToCanvasMatrix;

    private int frameWidth;
    private int frameHeight;

    private int sensorOrientation;
    private Context context;

    public MultiBoxTracker(final Context context) {
        this.context = context;
        for (final int color : COLORS) {
            availableColors.add(color);
        }

        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Style.STROKE);
        boxPaint.setStrokeWidth(12.0f);
        boxPaint.setStrokeCap(Cap.ROUND);
        boxPaint.setStrokeJoin(Join.ROUND);
        boxPaint.setStrokeMiter(100);

        textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, context.getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);

        initData();
        Log.e("yang", left_x_a + " " + left_y_b + " " + right_x_a + " " + right_y_b + " " + SysApplication.Screen_px_width + " " + SysApplication.Screen_px_height);
    }

    private enum Event {
        front_person(0), front_bicycle(1), front_car(2), front_traffic_light(3), front_stop_sign(4),
        left_person(5), right_person(6), left_bicycle(7), right_bicycle(8), left_car(9), right_car(10);
        private final int value;

        private Event(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    class AlarmEvent {
        public Event event;
        public double distance;

        AlarmEvent(Event event, double distance) {
            this.event = event;
            this.distance = distance;
        }
    }

    class MyComparator implements Comparator<AlarmEvent> {

        @Override
        public int compare(AlarmEvent t1, AlarmEvent t2) {
            if (t1.event.value() == t2.event.value() && t1.distance == t2.distance)
                return 0;
            if ((t1.event.value() <= 4 && t2.event.value() <= 4) || (t1.event.value() > 4 && t2.event.value() > 4)) {
                return t1.distance < t2.distance ? -1 : 1;
            } else {
                return t1.event.value() < t2.event.value() ? -1 : 1;
            }
        }
    }

    private void initData() {
        mediaPlayer = new MediaPlayer();
        event2rawId.put(0, R.raw.front_person);
        event2rawId.put(1, R.raw.front_bicycle);
        event2rawId.put(2, R.raw.front_car);
        event2rawId.put(3, R.raw.front_traffic_light);
        event2rawId.put(4, R.raw.front_stop_sign);
        event2rawId.put(5, R.raw.left_person);
        event2rawId.put(6, R.raw.right_person);
        event2rawId.put(7, R.raw.left_bicycle);
        event2rawId.put(8, R.raw.right_bicycle);
        event2rawId.put(9, R.raw.left_car);
        event2rawId.put(10, R.raw.right_car);

        mediaPlayer = new MediaPlayer();
        priorityQueue = new PriorityQueue<>(10, new MyComparator());
    }

    private Matrix getFrameToCanvasMatrix() {
        return frameToCanvasMatrix;
    }

    public synchronized void drawDebug(final Canvas canvas) {
        final Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60.0f);

        final Paint boxPaint = new Paint();
        boxPaint.setColor(Color.RED);
        boxPaint.setAlpha(200);
        boxPaint.setStyle(Style.STROKE);

        for (final Pair<Float, RectF> detection : screenRects) {
            final RectF rect = detection.second;
            canvas.drawRect(rect, boxPaint);
            canvas.drawText("" + detection.first, rect.left, rect.top, textPaint);
            borderedText.drawText(canvas, rect.centerX(), rect.centerY(), "" + detection.first);
        }

        if (objectTracker == null) {
            return;
        }

        // Draw correlations.
        for (final TrackedRecognition recognition : trackedObjects) {
            final ObjectTracker.TrackedObject trackedObject = recognition.trackedObject;

            final RectF trackedPos = trackedObject.getTrackedPositionInPreviewFrame();

            if (getFrameToCanvasMatrix().mapRect(trackedPos)) {
                final String labelString = String.format("%.2f", trackedObject.getCurrentCorrelation());
                borderedText.drawText(canvas, trackedPos.right, trackedPos.bottom, labelString);
            }
        }

        final Matrix matrix = getFrameToCanvasMatrix();
        objectTracker.drawDebug(canvas, matrix);
    }

    public synchronized void trackResults(
            final List<Recognition> results, final byte[] frame, final long timestamp) {
        logger.i("Processing %d results from %d", results.size(), timestamp);
        processResults(timestamp, results, frame);
    }

    public synchronized void draw(final Canvas canvas) {
        //主车道标识线
        boxPaint.setColor(Color.RED);
        canvas.drawLine(left_start_x, left_start_y, left_end_x, left_end_y, boxPaint);
        canvas.drawLine(right_start_x, right_start_y, right_end_x, right_end_y, boxPaint);
//        boxPaint.setColor(Color.BLUE);
//        canvas.drawLine(right_end_x, right_end_y, right_end_x2, right_end_y2, boxPaint);
//        canvas.drawLine(left_end_x, left_end_y, left_end_x2, left_end_y2, boxPaint);
        boxPaint.setColor(Color.BLACK);
        canvas.drawPoint(intersection_x, intersection_y, boxPaint);
        final boolean rotated = sensorOrientation % 180 == 90;
        final float multiplier =
                Math.min(canvas.getHeight() / (float) (rotated ? frameWidth : frameHeight),
                        canvas.getWidth() / (float) (rotated ? frameHeight : frameWidth));
        frameToCanvasMatrix =
                ImageUtils.getTransformationMatrix(
                        frameWidth,
                        frameHeight,
                        (int) (multiplier * (rotated ? frameHeight : frameWidth)),
                        (int) (multiplier * (rotated ? frameWidth : frameHeight)),
                        sensorOrientation,
                        false);
        for (final TrackedRecognition recognition : trackedObjects) {
            final RectF trackedPos =
                    (objectTracker != null)
                            ? recognition.trackedObject.getTrackedPositionInPreviewFrame()
                            : new RectF(recognition.location);

            getFrameToCanvasMatrix().mapRect(trackedPos);
            boxPaint.setColor(recognition.color);

            final float cornerSize = Math.min(trackedPos.width(), trackedPos.height()) / 8.0f;
            canvas.drawRoundRect(trackedPos, cornerSize, cornerSize, boxPaint);

            final String labelString =
                    !TextUtils.isEmpty(recognition.title)
                            ? String.format("%s %.2f", recognition.title, recognition.detectionConfidence)
                            : String.format("%.2f", recognition.detectionConfidence);

            borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.bottom, labelString + "\n");

            String title = recognition.title;
            if (title.equals("person")) {
                float center_x = (trackedPos.left + trackedPos.right) / 2;
                float center_y = (trackedPos.top + trackedPos.bottom) / 2;
                if (center_y > intersection_y && center_x / left_x_a + center_y / left_y_b - 1 < 0) {
                    priorityQueue.add(new AlarmEvent(Event.left_person, 5 * (trackedPos.right - trackedPos.left)));
                } else if (center_y > intersection_y && center_x / right_x_a + center_y / right_y_b - 1 < 0) {
                    priorityQueue.add(new AlarmEvent(Event.right_person, 5 * (trackedPos.right - trackedPos.left)));
                } else
                    priorityQueue.add(new AlarmEvent(Event.front_person, 5 * (trackedPos.right - trackedPos.left)));
            } else if (title.equals("car") || title.equals("bus") || title.equals("truck")) {
                float center_x = (trackedPos.left + trackedPos.right) / 2;
                float center_y = (trackedPos.top + trackedPos.bottom) / 2;
                if (center_y > intersection_y && center_x / left_x_a + center_y / left_y_b - 1 < 0) {
                    priorityQueue.add(new AlarmEvent(Event.left_car, 1.5 * (trackedPos.right - trackedPos.left)));
                } else if (center_y > intersection_y && center_x / right_x_a + center_y / right_y_b - 1 < 0) {
                    priorityQueue.add(new AlarmEvent(Event.right_car, 1.5 * (trackedPos.right - trackedPos.left)));
                } else
                    priorityQueue.add(new AlarmEvent(Event.front_car, 1.5 * (trackedPos.right - trackedPos.left)));
            } else if (title.equals("bicycle") || title.equals("motorcycle")) {
                float center_x = (trackedPos.left + trackedPos.right) / 2;
                float center_y = (trackedPos.top + trackedPos.bottom) / 2;
                if (center_y > intersection_y && center_x / left_x_a + center_y / left_y_b - 1 < 0) {
                    priorityQueue.add(new AlarmEvent(Event.left_bicycle, trackedPos.right - trackedPos.left));
                } else if (center_y > intersection_y && center_x / right_x_a + center_y / right_y_b - 1 < 0) {
                    priorityQueue.add(new AlarmEvent(Event.right_bicycle, trackedPos.right - trackedPos.left));
                } else
                    priorityQueue.add(new AlarmEvent(Event.front_bicycle, trackedPos.right - trackedPos.left));
            } else if (title.equals("traffic light")) {
                priorityQueue.add(new AlarmEvent(Event.front_traffic_light, 10 * (trackedPos.right - trackedPos.left)));
            } else if (title.equals("stop sign")) {
                priorityQueue.add(new AlarmEvent(Event.front_stop_sign, 4 * (trackedPos.right - trackedPos.left)));
            }
//            borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.bottom + cornerSize * 1 + 10, trackedPos.left + "");
//            borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.bottom + cornerSize * 2 + 10, trackedPos.top + "");
//            borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.bottom + cornerSize * 3 + 10, trackedPos.right + "");
//            borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.bottom + cornerSize * 4 + 10, trackedPos.bottom + "");
//            borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.bottom + cornerSize * 5 + 10, (trackedPos.right - trackedPos.left) + "");
//            borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.bottom + cornerSize * 6 + 10, (trackedPos.bottom - trackedPos.top) + "");
        }
        if (!priorityQueue.isEmpty()) {
            if (!mediaPlayer.isPlaying()) {
                Event event = priorityQueue.peek().event;
                mediaPlayer = MediaPlayer.create(context, event2rawId.get(event.value()));
                mediaPlayer.start();
            }
            priorityQueue = new PriorityQueue<>(10, new MyComparator());
        }
    }

    private boolean initialized = false;

    public synchronized void onFrame(
            final int w,
            final int h,
            final int rowStride,
            final int sensorOrientation,
            final byte[] frame,
            final long timestamp) {
        if (objectTracker == null && !initialized) {
            ObjectTracker.clearInstance();

            logger.i("Initializing ObjectTracker: %dx%d", w, h);
            objectTracker = ObjectTracker.getInstance(w, h, rowStride, true);
            frameWidth = w;
            frameHeight = h;
            this.sensorOrientation = sensorOrientation;
            initialized = true;

            if (objectTracker == null) {
                String message =
                        "Object tracking support not found. "
                                + "See tensorflow/examples/android/README.md for details.";
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                logger.e(message);
            }
        }

        if (objectTracker == null) {
            return;
        }

        objectTracker.nextFrame(frame, null, timestamp, null, true);

        // Clean up any objects not worth tracking any more.
        final LinkedList<TrackedRecognition> copyList =
                new LinkedList<TrackedRecognition>(trackedObjects);
        for (final TrackedRecognition recognition : copyList) {
            final ObjectTracker.TrackedObject trackedObject = recognition.trackedObject;
            final float correlation = trackedObject.getCurrentCorrelation();
            if (correlation < MIN_CORRELATION) {
                logger.v("Removing tracked object %s because NCC is %.2f", trackedObject, correlation);
                trackedObject.stopTracking();
                trackedObjects.remove(recognition);

                availableColors.add(recognition.color);
            }
        }
    }

    private void processResults(

            final long timestamp, final List<Recognition> results, final byte[] originalFrame) {
        final List<Pair<Float, Recognition>> rectsToTrack = new LinkedList<Pair<Float, Recognition>>();

        screenRects.clear();
        final Matrix rgbFrameToScreen = new Matrix(getFrameToCanvasMatrix());

        for (final Recognition result : results) {
            if (result.getLocation() == null) {
                continue;
            }
            final RectF detectionFrameRect = new RectF(result.getLocation());
            final RectF detectionScreenRect = new RectF();
            rgbFrameToScreen.mapRect(detectionScreenRect, detectionFrameRect);

            Log.e("Like",
                    "Result! Frame: " + result.getLocation() + " mapped to screen:" + " Like " + detectionScreenRect.left + " " + detectionScreenRect.right + " " + detectionScreenRect.bottom + " " + detectionScreenRect.top);

            screenRects.add(new Pair<Float, RectF>(result.getConfidence(), detectionScreenRect));

            if (detectionFrameRect.width() < MIN_SIZE || detectionFrameRect.height() < MIN_SIZE) {
                logger.w("Degenerate rectangle! " + detectionFrameRect);
                continue;
            }

            rectsToTrack.add(new Pair<Float, Recognition>(result.getConfidence(), result));
        }

        if (rectsToTrack.isEmpty()) {
            logger.v("Nothing to track, aborting.");
            return;
        }

        if (objectTracker == null) {
            trackedObjects.clear();
            for (final Pair<Float, Recognition> potential : rectsToTrack) {
                final TrackedRecognition trackedRecognition = new TrackedRecognition();
                trackedRecognition.detectionConfidence = potential.first;
                trackedRecognition.location = new RectF(potential.second.getLocation());
                trackedRecognition.trackedObject = null;
                trackedRecognition.title = potential.second.getTitle();
                trackedRecognition.color = COLORS[trackedObjects.size()];
                trackedObjects.add(trackedRecognition);

                if (trackedObjects.size() >= COLORS.length) {
                    break;
                }
            }
            return;
        }

        logger.i("%d rects to track", rectsToTrack.size());
        for (final Pair<Float, Recognition> potential : rectsToTrack) {
            handleDetection(originalFrame, timestamp, potential);
        }
    }

    private void handleDetection(
            final byte[] frameCopy, final long timestamp, final Pair<Float, Recognition> potential) {
        final ObjectTracker.TrackedObject potentialObject =
                objectTracker.trackObject(potential.second.getLocation(), timestamp, frameCopy);

        final float potentialCorrelation = potentialObject.getCurrentCorrelation();
        logger.v(
                "Tracked object went from %s to %s with correlation %.2f",
                potential.second, potentialObject.getTrackedPositionInPreviewFrame(), potentialCorrelation);

        if (potentialCorrelation < MARGINAL_CORRELATION) {
            logger.v("Correlation too low to begin tracking %s.", potentialObject);
            potentialObject.stopTracking();
            return;
        }

        final List<TrackedRecognition> removeList = new LinkedList<TrackedRecognition>();

        float maxIntersect = 0.0f;

        // This is the current tracked object whose color we will take. If left null we'll take the
        // first one from the color queue.
        TrackedRecognition recogToReplace = null;

        // Look for intersections that will be overridden by this object or an intersection that would
        // prevent this one from being placed.
        for (final TrackedRecognition trackedRecognition : trackedObjects) {
            final RectF a = trackedRecognition.trackedObject.getTrackedPositionInPreviewFrame();
            final RectF b = potentialObject.getTrackedPositionInPreviewFrame();
            final RectF intersection = new RectF();
            final boolean intersects = intersection.setIntersect(a, b);

            final float intersectArea = intersection.width() * intersection.height();
            final float totalArea = a.width() * a.height() + b.width() * b.height() - intersectArea;
            final float intersectOverUnion = intersectArea / totalArea;

            // If there is an intersection with this currently tracked box above the maximum overlap
            // percentage allowed, either the new recognition needs to be dismissed or the old
            // recognition needs to be removed and possibly replaced with the new one.
            if (intersects && intersectOverUnion > MAX_OVERLAP) {
                if (potential.first < trackedRecognition.detectionConfidence
                        && trackedRecognition.trackedObject.getCurrentCorrelation() > MARGINAL_CORRELATION) {
                    // If track for the existing object is still going strong and the detection score was
                    // good, reject this new object.
                    potentialObject.stopTracking();
                    return;
                } else {
                    removeList.add(trackedRecognition);

                    // Let the previously tracked object with max intersection amount donate its color to
                    // the new object.
                    if (intersectOverUnion > maxIntersect) {
                        maxIntersect = intersectOverUnion;
                        recogToReplace = trackedRecognition;
                    }
                }
            }
        }

        // If we're already tracking the max object and no intersections were found to bump off,
        // pick the worst current tracked object to remove, if it's also worse than this candidate
        // object.
        if (availableColors.isEmpty() && removeList.isEmpty()) {
            for (final TrackedRecognition candidate : trackedObjects) {
                if (candidate.detectionConfidence < potential.first) {
                    if (recogToReplace == null
                            || candidate.detectionConfidence < recogToReplace.detectionConfidence) {
                        // Save it so that we use this color for the new object.
                        recogToReplace = candidate;
                    }
                }
            }
            if (recogToReplace != null) {
                logger.v("Found non-intersecting object to remove.");
                removeList.add(recogToReplace);
            } else {
                logger.v("No non-intersecting object found to remove");
            }
        }

        // Remove everything that got intersected.
        for (final TrackedRecognition trackedRecognition : removeList) {
            logger.v(
                    "Removing tracked object %s with detection confidence %.2f, correlation %.2f",
                    trackedRecognition.trackedObject,
                    trackedRecognition.detectionConfidence,
                    trackedRecognition.trackedObject.getCurrentCorrelation());
            trackedRecognition.trackedObject.stopTracking();
            trackedObjects.remove(trackedRecognition);
            if (trackedRecognition != recogToReplace) {
                availableColors.add(trackedRecognition.color);
            }
        }

        if (recogToReplace == null && availableColors.isEmpty()) {
            logger.e("No room to track this object, aborting.");
            potentialObject.stopTracking();
            return;
        }

        // Finally safe to say we can track this object.
        logger.v(
                "Tracking object %s (%s) with detection confidence %.2f at position %s",
                potentialObject,
                potential.second.getTitle(),
                potential.first,
                potential.second.getLocation());
        final TrackedRecognition trackedRecognition = new TrackedRecognition();
        trackedRecognition.detectionConfidence = potential.first;
        trackedRecognition.trackedObject = potentialObject;
        trackedRecognition.title = potential.second.getTitle();

        // Use the color from a replaced object before taking one from the color queue.
        trackedRecognition.color =
                recogToReplace != null ? recogToReplace.color : availableColors.poll();
        trackedObjects.add(trackedRecognition);
    }
}
