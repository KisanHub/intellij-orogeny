/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.commandLine;

import com.intellij.ide.Bootstrap;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.intellij.idea.Main.setFlags;
import static com.intellij.openapi.application.PathManager.PROPERTY_HOME_PATH;
import static com.kisanhub.intellij.useful.commandLine.commandLineApplicationStarterExs.AbstractCommandLineApplicationStarterEx.UnexpectedErrorExitCode;
import static java.lang.Boolean.TRUE;
import static java.lang.System.*;

public final class ExternalDriver
{
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
	private final String homePath;

	public ExternalDriver(@NotNull final String homePath)
	{
		this.homePath = homePath;
	}

	public void invoke(@NotNull final String abstractCommandLineApplicationStarterExClassName, @NotNull final String... commandLineArguments)
	{
		forceIntelliJHome();
		forceIntelliJCommunityEditionToBeHeadlessAndInCommandLineMode();
		invokeIntelliJWithOurApplication(abstractCommandLineApplicationStarterExClassName, commandLineArguments);
	}

	@SuppressWarnings("AccessOfSystemProperties")
	private void forceIntelliJHome()
	{
		setProperty(PROPERTY_HOME_PATH, homePath);
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
			Bootstrap.main(intelliJCommandLineArguments, MainImplReplacement, "start");
		}
		catch (final Exception e)
		{
			e.printStackTrace(err);
			exit(UnexpectedErrorExitCode);
		}
	}
}
