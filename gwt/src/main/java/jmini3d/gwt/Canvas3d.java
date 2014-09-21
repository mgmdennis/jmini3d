package jmini3d.gwt;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.dom.client.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;

import jmini3d.Scene;
import jmini3d.SceneController;

public class Canvas3d implements AnimationScheduler.AnimationCallback {

	Element webGLCanvas;
	WebGLRenderingContext gl;

	boolean stopped = false;
	int width, height;

	public Renderer3d renderer3d;
	SceneController sceneController;
	boolean renderContinuously;

	public Canvas3d(String resourceLoaderDir, SceneController sceneController, boolean renderContinuously) {
		this.sceneController = sceneController;
		webGLCanvas = DOM.createElement("canvas");
		this.renderContinuously = renderContinuously;

		gl = (WebGLRenderingContext) getContext(webGLCanvas, "webgl");
		if (gl == null) {
			gl = (WebGLRenderingContext) getContext(webGLCanvas, "experimental-webgl");
		}
		if (gl == null) {
			Window.alert("Sorry, Your browser doesn't support WebGL. Please Install the last version of Firefox, Chrome or Internet Explorer (>=11).");
			return;
		}

		renderer3d = new Renderer3d(gl, new ResourceLoader(resourceLoaderDir));
	}

	public final native Context getContext(Element el, String contextId) /*-{
    	return el.getContext(contextId);
 	}-*/;

	public void setSize(int width, int heigth) {
		this.width = width;
		this.height = heigth;
		webGLCanvas.setAttribute("width", width + "px");
		webGLCanvas.setAttribute("height", heigth + "px");
	}

	public void onResume() {
		stopped = false;
		if (renderContinuously) {
			AnimationScheduler.get().requestAnimationFrame(this);
		} else {
			requestRender();
		}
	}

	public void onPause() {
		stopped = true;
	}

	@Override
	public void execute(double timestamp) {
		if (!stopped) {
			if (sceneController != null) {
				Scene scene = sceneController.getScene(width, height);
				renderer3d.render(scene);
			}
			AnimationScheduler.get().requestAnimationFrame(this);
		}
	}

	public void requestRender() {
		Scene scene = sceneController.getScene(width, height);
		renderer3d.render(scene);
	}

	public void setSceneController(SceneController sceneController) {
		this.sceneController = sceneController;
	}

	public Renderer3d getRenderer3d() {
		return renderer3d;
	}

	public Element getElement() {
		return webGLCanvas;
	}
}