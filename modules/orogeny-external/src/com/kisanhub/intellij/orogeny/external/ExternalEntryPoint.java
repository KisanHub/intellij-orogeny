/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.external;

import com.kisanhub.intellij.useful.commandLine.ExternalDriver;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static com.kisanhub.intellij.useful.commandLine.ExternalDriver.MacOsXHomePath;
import static java.lang.String.format;
import static java.lang.System.err;
import static java.lang.System.exit;
import static java.lang.System.out;
import static java.util.Locale.ENGLISH;

public final class ExternalEntryPoint
{
	private static final int ExitCodeHelp = 0;

	private static final int ExitCodeError = 1;

	private static final int ExitCodeUnexpectedError = 2;

	@SuppressWarnings("ConstantNamingConvention")
	@NotNull
	private static final String help = "help";

	@SuppressWarnings("ConstantNamingConvention")
	@NotNull
	private static final String home = "home";

	@SuppressWarnings("ConstantNamingConvention")
	@NotNull
	private static final String project = "project";

	@NonNls
	@NotNull
	private static final OptionParser CommandLineArgumentsParser = new OptionParser();

	@NonNls
	@NotNull
	private static final String PATH = "PATH";

	static
	{
		CommandLineArgumentsParser.posixlyCorrect(true);

		CommandLineArgumentsParser.accepts(help, "show help").forHelp();

		CommandLineArgumentsParser.accepts(home, "IntelliJ home path").withRequiredArg().describedAs(PATH).ofType(File.class).defaultsTo(new File(MacOsXHomePath));

		CommandLineArgumentsParser.accepts(project, "IntelliJ project to build path").withRequiredArg().describedAs(PATH).ofType(File.class);
	}

	// Has to be done this way to prevent the class loading on a different ClassLoader before IntelliJ initialised
	@NotNull
	@NonNls
	private static final String abstractCommandLineApplicationStarterExClassName = "com.kisanhub.intellij.orogeny.plugin.BuilderCommandLineApplicationStarterEx";

	public static void main(@NotNull final String... commandLineArguments)
	{
		@NotNull final OptionSet arguments;
		try
		{
			arguments = CommandLineArgumentsParser.parse(commandLineArguments);
		}
		catch (final OptionException e)
		{
			printExceptionAndHelpThenExit(e);
			throw newImpossibleException();
		}

		if (arguments.has(help))
		{
			printHelpThenExit(ExitCodeHelp);
			throw newImpossibleException();
		}

		final File homePath = getExtantFileOption(arguments, home);
		if (!homePath.isDirectory())
		{
			printErrorMessageAndHelpThenExit("Please specify the option --%1$s with an extant directory path", home);
			throw newImpossibleException();
		}

		final File projectPath = getExtantFileOption(arguments, project);

		new ExternalDriver(homePath.getPath()).invoke(abstractCommandLineApplicationStarterExClassName, projectPath.getPath());
	}

	@NotNull
	private static File getExtantFileOption(@NotNull final OptionSet arguments, @NotNull @NonNls final String argument)
	{
		final File path = getOption(arguments, argument);
		if (path.exists())
		{
			return path;
		}

		printErrorMessageAndHelpThenExit("Please specify the option --%1$s with an extant path", argument);
		throw newImpossibleException();
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private static <V> V getOption(@NotNull final OptionSet arguments, @NotNull @NonNls final String argument)
	{
		if (!arguments.has(argument))
		{
			printErrorMessageAndHelpThenExit("Please specify the option --%1$s", argument);
			throw newImpossibleException();
		}

		if (!arguments.hasArgument(argument))
		{
			printErrorMessageAndHelpThenExit("Please specify the option --%1$s with an argument", argument);
			throw newImpossibleException();
		}

		final V optionValue;
		try
		{
			optionValue = (V) arguments.valueOf(argument);
		}
		catch (final OptionException e)
		{
			printErrorMessageAndHelpThenExit("Please specify the option --%1$s with only one argument", argument);
			throw newImpossibleException();
		}

		// This assertion is valid because we have checked hasArgument() earlier
		assert optionValue != null;
		return optionValue;
	}

	@NotNull
	private static IllegalStateException newImpossibleException()
	{
		return new IllegalStateException("Impossible");
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static void printExceptionAndHelpThenExit(@NotNull final Exception e)
	{
		printErrorMessageAndHelpThenExit("%1$s", e.getMessage());
	}

	private static void printErrorMessageAndHelpThenExit(@NonNls @NotNull final String messageTemplate, @NotNull final String... arguments)
	{
		printErrorMessage(messageTemplate, arguments);
		printHelpThenExit(ExitCodeError);
	}

	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "CallToSystemExit"})
	private static void printHelpThenExit(final int exitCode)
	{
		try
		{
			CommandLineArgumentsParser.printHelpOn(out);
		}
		catch (final IOException e)
		{
			printUnexpectedFailureAndExitAbnormally(e);
		}
		exit(exitCode);
	}

	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "CallToPrintStackTrace", "CallToSystemExit"})
	private static void printUnexpectedFailureAndExitAbnormally(@NotNull final Throwable throwable)
	{
		printErrorMessage("Unexpected failure %1$s", throwable.getMessage());
		throwable.printStackTrace(err);
		exit(ExitCodeUnexpectedError);
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static void printErrorMessage(@NotNull final String messageTemplate, @NotNull final String... arguments)
	{
		err.println(format(ENGLISH, messageTemplate, arguments));
	}

	private ExternalEntryPoint()
	{
	}
}
