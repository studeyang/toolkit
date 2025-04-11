//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.github.toolkit.web;

import org.springframework.beans.factory.annotation.Value;

public abstract class BaseController {
	@Value("${server.contextPath:''}")
	private String contextPath;
	private String controllerContext;

	protected BaseController(String controllerContext) {
		this.controllerContext = controllerContext;
	}

	protected String getFullPath(String path) {
		return this.contextPath + this.controllerContext + path;
	}
}
