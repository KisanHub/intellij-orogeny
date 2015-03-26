/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.commandLine;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.intellij.ide.Bootstrap.main;
import static com.intellij.idea.Main.setFlags;
import static com.intellij.openapi.application.PathManager.PROPERTY_HOME_PATH;
import static java.lang.Boolean.TRUE;
import static java.lang.System.*;
import static java.lang.Thread.currentThread;

public final class ExternalDriver
{
	@SuppressWarnings("HardcodedFileSeparator")
	@NonNls
	@NotNull
	public static final String MacOsXHomePath = "/Applications/IntelliJ IDEA 14 CE.app/Contents";

	@NotNull
	@NonNls
	private static final String JavaAwtHeadless = "java.awt.headless";

	@NotNull
	private static final String[] Empty = {};

	@NotNull
	@NonNls
	private static final String MainImplReplacement = "com.intellij.idea.MainImplReplacement";

	@SuppressWarnings("ConstantConditions")
	@NotNull
	@NonNls
	private static final String BooleanTrueString = TRUE.toString();

	@NotNull
	@NonNls
	private final File homePath;

	public ExternalDriver(@NotNull final File homePath)
	{
		this.homePath = homePath;
	}

	public void invoke(@NotNull final String abstractCommandLineApplicationStarterExClassName, @NotNull final String... commandLineArguments)
	{
		forceIntelliJJarsOntoClassPath();
		forceIntelliJHome();
		forceIntelliJCommunityEditionToBeHeadlessAndInCommandLineMode();
		invokeIntelliJWithOurApplication(abstractCommandLineApplicationStarterExClassName, commandLineArguments);
	}

	private void forceIntelliJJarsOntoClassPath()
	{
		final File intelliJJarPath = new File(homePath, "lib");
		final Class<? extends ExternalDriver> aClass = getClass();
		assert aClass != null;
		@SuppressWarnings("ClassLoaderInstantiation") final JarFileClassLoader jarFileClassLoader = new JarFileClassLoader(aClass.getClassLoader(), intelliJJarPath);
		currentThread().setContextClassLoader(jarFileClassLoader);
	}

	@SuppressWarnings("AccessOfSystemProperties")
	private void forceIntelliJHome()
	{
		setProperty(PROPERTY_HOME_PATH, homePath.getPath());
	}

	@SuppressWarnings("AccessOfSystemProperties")
	private static void forceIntelliJCommunityEditionToBeHeadlessAndInCommandLineMode()
	{
		setProperty(JavaAwtHeadless, BooleanTrueString);
		setFlags(Empty);
	}

	@SuppressWarnings({"StringConcatenationMissingWhitespace", "UseOfSystemOutOrSystemErr", "CallToSystemExit"})
	private static void invokeIntelliJWithOurApplication(@NotNull final String abstractCommandLineApplicationStarterExClassName, @NotNull final String... commandLineArguments)
	{
		final int length = commandLineArguments.length;
		final String[] intelliJCommandLineArguments = new String[length + 1];
		intelliJCommandLineArguments[0] = abstractCommandLineApplicationStarterExClassName;
		arraycopy(commandLineArguments, 0, intelliJCommandLineArguments, 1, length);

		try
		{
			main(intelliJCommandLineArguments, MainImplReplacement, "start");
		}
		catch (final Exception e)
		{
			throw new IllegalStateException("Couldn't invoke Bootstrap main()", e);
		}
	}
}
