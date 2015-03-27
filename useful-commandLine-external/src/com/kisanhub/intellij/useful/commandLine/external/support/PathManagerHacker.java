/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.commandLine.external.support;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static java.lang.System.setProperty;

public final class PathManagerHacker
{
	@SuppressWarnings("HardcodedFileSeparator")
	@NonNls
	@NotNull
	public static final String MacOsXHomePath = "/Applications/IntelliJ IDEA 14 CE.app/Contents";

	@NonNls
	@NotNull
	public static final String DefaultConfigFolderName = "IdeaIC14";

	// com.intellij.openapi.application.PathManager.PROPERTY_HOME_PATH
	@NotNull
	@NonNls
	private static final String PROPERTY_HOME_PATH = "idea.home.path";

	// com.intellij.openapi.application.PathManager.PROPERTY_PATHS_SELECTOR_PATH
	@NotNull
	@NonNls
	private static final String PROPERTY_PATHS_SELECTOR = "idea.paths.selector";

	// com.intellij.openapi.application.PathManager.LIB_FOLDER
	@NotNull
	@NonNls
	private static final String LIB_FOLDER = "lib";

	@NotNull
	private final File homePath;

	@NotNull
	@NonNls
	private final String configFolderName;

	public PathManagerHacker(@NotNull final File homePath, @NotNull @NonNls final String configFolderName)
	{
		this.homePath = homePath;
		this.configFolderName = configFolderName;
	}

	public void configureIntelliJPathManager()
	{
		forceIntelliJHome();
		forceIntelliJConfigPath();
	}

	@SuppressWarnings("AccessOfSystemProperties")
	private void forceIntelliJHome()
	{
		setProperty(PROPERTY_HOME_PATH, homePath.getPath());
	}

	@SuppressWarnings("AccessOfSystemProperties")
	private void forceIntelliJConfigPath()
	{
		setProperty(PROPERTY_PATHS_SELECTOR, configFolderName);
	}

	@NotNull
	public File intelliJJarPath()
	{
		return new File(homePath, LIB_FOLDER);
	}
}
