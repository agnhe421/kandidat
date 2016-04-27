package com.qualcomm.vuforia.samples.Vuforia;

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


        import com.badlogic.gdx.graphics.Camera;
        import com.badlogic.gdx.math.Matrix4;
        import com.badlogic.gdx.math.Vector3;
        import com.qualcomm.vuforia.samples.singletons.DataHolder;

/** A Camera with perspective projection.
 *
 * @author mzechner */
public class VuforiaCamera extends Camera {
    /** the field of view of the height, in degrees **/
    public float fieldOfView = 67;

    public VuforiaCamera () {
    }

    /** Constructs a new with the given field of view and viewport size. The aspect ratio is derived from
     * the viewport size.
     *
     * @param fieldOfViewY the field of view of the height, in degrees, the field of view for the width will be calculated
     *           according to the aspect ratio.
     * @param viewportWidth the viewport width
     * @param viewportHeight the viewport height */
    public VuforiaCamera (float fieldOfViewY, float viewportWidth, float viewportHeight) {
        this.fieldOfView = fieldOfViewY;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        update();
    }

    final Vector3 tmp = new Vector3();

    @Override
    public void update () {
        update(true);
    }

    @Override
    public void update (boolean updateFrustum) {
        float aspect = viewportWidth / viewportHeight;

        float[] ViewMatrix = DataHolder.getInstance().getData();
        float[] ProjMatrix = DataHolder.getInstance().getData2();

        if(ViewMatrix != null && ProjMatrix != null) {

            Matrix4 temp = new Matrix4(ViewMatrix.clone());
            Matrix4 temp2 = new Matrix4(ProjMatrix.clone());

            temp.rotate(1,0,0,90);

            projection.set(temp2);
            view.set(temp);

//            projection.setToProjection(Math.abs(near), Math.abs(far), fieldOfView, aspect);
//            view.setToLookAt(position, tmp.set(position).add(direction), up);

            combined.set(projection);
            Matrix4.mul(combined.val, view.val);

            if (updateFrustum) {
                invProjectionView.set(combined);
                Matrix4.inv(invProjectionView.val);
                frustum.update(invProjectionView);
            }

        }


    }
}