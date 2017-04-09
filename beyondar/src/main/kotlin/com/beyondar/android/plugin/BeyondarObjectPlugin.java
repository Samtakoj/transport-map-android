/*
 * Copyright (C) 2014 BeyondAR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.beyondar.android.plugin;

import com.beyondar.android.opengl.renderable.Renderable;
import com.beyondar.android.opengl.texture.Texture;
import com.beyondar.android.util.math.geom.Point3;
import com.beyondar.android.world.BeyondarObject;

/**
 * Basic interface to create a plugin for a
 * {@link com.beyondar.android.world.BeyondarObject BeyondarObject}.
 * 
 */
public interface BeyondarObjectPlugin extends Plugin {

	/**
	 * Called when the angle of the
	 * {@link com.beyondar.android.world.BeyondarObject BeyondarObject} is
	 * changed.
	 * 
	 * @param angle
	 *            New angle.
	 */
	void onAngleChanged(Point3 angle);

	/**
	 * Called when the position of the
	 * {@link com.beyondar.android.world.BeyondarObject BeyondarObject} has
	 * changed.
	 * 
	 * @param position
	 *            New position.
	 */
	void onPositionChanged(Point3 position);

	/**
	 * Called when the {@link com.beyondar.android.opengl.texture.Texture
	 * Texture}1 of the {@link com.beyondar.android.world.BeyondarObject
	 * BeyondarObject} has been changed.
	 * 
	 * @param texture
	 *            New texture.
	 */
	void onTextureChanged(Texture texture);

	/**
	 * Called when the {@link com.beyondar.android.opengl.renderable.Renderable
	 * Renderable} of the {@link com.beyondar.android.world.BeyondarObject
	 * BeyondarObject} has been changed.
	 * 
	 * @param renderable
	 *            New Renderable.
	 */
	void onRenderableChanged(Renderable renderable);

	/**
	 * Called when the {@link com.beyondar.android.world.BeyondarObject
	 * BeyondarObject} changes the value for facing the camera.
	 * 
	 * @param faceToCamera
	 *            True if it is facing to the camera, false otherwise.
	 */
	void onFaceToCameraChanged(boolean faceToCamera);

	/**
	 * Called when the visibility of the
	 * {@link com.beyondar.android.world.BeyondarObject BeyondarObject} has been
	 * changed.
	 * 
	 * @param visible
	 *            True if it is visible, false otherwise.
	 */
	void onVisibilityChanged(boolean visible);

	/**
	 * Called when the name of the
	 * {@link com.beyondar.android.world.BeyondarObject BeyondarObject} has been
	 * changed.
	 * 
	 * @param name
	 *            New name.
	 */
	void onNameChanged(String name);

	/**
	 * Called when the image uri of the
	 * {@link com.beyondar.android.world.BeyondarObject BeyondarObject} has been
	 * changed.
	 * 
	 * @param uri
	 *            New image uri.
	 */
	void onImageUriChanged(String uri);

	/**
	 * This method is invoked when the plugin is removed.
	 */
	void onDetached();

	/**
	 * Check if the plugin is attached.
	 * 
	 * @return
	 */
	boolean isAttached();

	/**
	 * Get the {@link com.beyondar.android.world.BeyondarObject BeyondarObject}
	 * where the plugin is attached.
	 * 
	 * @return
	 */
	BeyondarObject getbeyondarObject();
}
