/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.external;

import com.kisanhub.intellij.useful.commandLine.ExternalDriver;
import joptsimple.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.kisanhub.intellij.orogeny.external.UsefulOptionParser.newImpossibleException;
import static com.kisanhub.intellij.orogeny.external.UsefulOptionParser.printUnexpectedFailureAndExitAbnormally;
import static com.kisanhub.intellij.useful.commandLine.ExternalDriver.MacOsXHomePath;
import static java.lang.String.format;

@SuppressWarnings("UtilityClass")
public final class EntryPoint
{
	@SuppressWarnings("ConstantNamingConvention")
	@NotNull
	private static final ArgumentAcceptingOptionSpec<File> home;

	@SuppressWarnings("ConstantNamingConvention")
	@NotNull
	private static final ArgumentAcceptingOptionSpec<File> project;

	@NonNls
	@NotNull
	private static final UsefulOptionParser CommandLineArgumentsParser = new UsefulOptionParser();

	@NonNls
	@NotNull
	private static final String PATH = "PATH";

	static
	{
		home = CommandLineArgumentsParser.accepts("home", "IntelliJ home path").withRequiredArg().describedAs(PATH).ofType(File.class).defaultsTo(new File(MacOsXHomePath));

		project = CommandLineArgumentsParser.accepts("project", "IntelliJ project to build path").withRequiredArg().describedAs(PATH).ofType(File.class);
	}

	// Has to be done this way to prevent the class loading on a different ClassLoader before IntelliJ initialised
	@NotNull
	@NonNls
	private static final String abstractCommandLineApplicationStarterExClassName = "com.kisanhub.intellij.orogeny.plugin.BuilderCommandLineApplicationStarterEx";

	public static void main(@NotNull final String... commandLineArguments)
	{
		@NotNull final UsefulOptionSet usefulOptionSet = CommandLineArgumentsParser.parse(commandLineArguments);

		final File homePath = usefulOptionSet.getExtantDirectoryFileOptionValue(home);

		final File projectPath = usefulOptionSet.getExtantFileOptionValue(project);

		try
		{
			new ExternalDriver(homePath).invoke(abstractCommandLineApplicationStarterExClassName, projectPath.getPath());
		}
		catch (final Throwable t)
		{
			printUnexpectedFailureAndExitAbnormally(t);
		}
	}
	
	private EntryPoint()
	{
	}
}
