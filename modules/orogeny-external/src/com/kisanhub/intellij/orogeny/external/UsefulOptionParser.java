/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.external;

import joptsimple.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static java.lang.String.format;
import static java.lang.System.err;
import static java.lang.System.exit;
import static java.lang.System.out;
import static java.util.Locale.ENGLISH;

public final class UsefulOptionParser
{
	private static final int ExitCodeHelp = 0;

	private static final int ExitCodeError = 1;

	private static final int ExitCodeUnexpectedError = 2;

	@SuppressWarnings("ConstantNamingConvention")
	@NotNull
	private final OptionSpec<?> help;

	@NotNull
	private final OptionParser optionParser;

	public UsefulOptionParser()
	{
		optionParser = new OptionParser();
		optionParser.posixlyCorrect(true);
		help = accepts("help", "show help").forHelp();
	}

	@NotNull
	public OptionSpecBuilder accepts(@NonNls @NotNull final String option, @NonNls @NotNull final String description)
	{
		return optionParser.accepts(option, description);
	}

	@NotNull
	public UsefulOptionSet parse(@NotNull final String... arguments)
	{
		@NotNull final OptionSet optionSet;
		try
		{
			optionSet = optionParser.parse(arguments);
		}
		catch (final OptionException e)
		{
			printExceptionAndHelpThenExit(e);
			//noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
			throw newImpossibleException();
		}

		if (optionSet.has(help))
		{
			printHelpThenExit(ExitCodeHelp);
			throw newImpossibleException();
		}
		return new UsefulOptionSet(this, optionSet);
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void printExceptionAndHelpThenExit(@NotNull final Exception e)
	{
		printErrorMessageAndHelpThenExit("%1$s", e.getMessage());
	}

	public void printErrorMessageAndHelpThenExit(@NonNls @NotNull final String messageTemplate, @NotNull final OptionDescriptor option)
	{
		printErrorMessageAndHelpThenExit(messageTemplate, firstOption(option));
	}

	public void printErrorMessageAndHelpThenExit(@NonNls @NotNull final String messageTemplate, @NotNull final String... arguments)
	{
		printErrorMessage(messageTemplate, arguments);
		printHelpThenExit(ExitCodeError);
	}

	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "CallToSystemExit"})
	private void printHelpThenExit(final int exitCode)
	{
		assert out != null;
		try
		{
			optionParser.printHelpOn(out);
		}
		catch (final IOException e)
		{
			printUnexpectedFailureAndExitAbnormally(e);
		}
		exit(exitCode);
	}

	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "CallToPrintStackTrace", "CallToSystemExit"})
	public static void printUnexpectedFailureAndExitAbnormally(@NotNull final Throwable throwable)
	{
		printErrorMessage("Unexpected failure %1$s", throwable.getMessage());
		assert err != null;
		throwable.printStackTrace(err);
		exit(ExitCodeUnexpectedError);
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static void printErrorMessage(@NonNls @NotNull final String messageTemplate, @NotNull final String... arguments)
	{
		assert err != null;
		err.println(format(ENGLISH, messageTemplate, arguments));
	}

	@NotNull
	public static IllegalStateException newImpossibleException()
	{
		return new IllegalStateException("Impossible");
	}

	@SuppressWarnings("LoopStatementThatDoesntLoop")
	@NotNull
	private static String firstOption(@NotNull final OptionDescriptor option)
	{
		for (final String optionName : option.options())
		{
			return optionName;
		}
		throw newImpossibleException();
	}
}
