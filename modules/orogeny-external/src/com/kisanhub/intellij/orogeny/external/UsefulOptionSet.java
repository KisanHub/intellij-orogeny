/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.external;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionException;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.kisanhub.intellij.orogeny.external.UsefulOptionParser.newImpossibleException;

public final class UsefulOptionSet
{
	@NotNull
	private final UsefulOptionParser usefulOptionParser;

	@NotNull
	private final OptionSet optionSet;

	public UsefulOptionSet(@NotNull final UsefulOptionParser usefulOptionParser, @NotNull final OptionSet optionSet)
	{
		this.usefulOptionParser = usefulOptionParser;
		this.optionSet = optionSet;
	}

	@NotNull
	public File getExtantDirectoryFileOptionValue(@NotNull @NonNls final ArgumentAcceptingOptionSpec<File> option)
	{
		final File path = getExtantFileOptionValue(option);
		if (!path.isDirectory())
		{
			usefulOptionParser.printErrorMessageAndHelpThenExit("Please specify the option --%1$s with an extant directory path", option);
			throw newImpossibleException();
		}
		return path;
	}

	@NotNull
	public File getExtantFileOptionValue(@NotNull @NonNls final ArgumentAcceptingOptionSpec<File> option)
	{
		final File path = getOptionValue(option);
		if (path.exists())
		{
			return path;
		}

		usefulOptionParser.printErrorMessageAndHelpThenExit("Please specify the option --%1$s with an extant path", option);
		throw newImpossibleException();
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public <V> V getOptionValue(@NotNull @NonNls final ArgumentAcceptingOptionSpec<V> option)
	{
		if (optionSet.has(option))
		{
			if (!optionSet.hasArgument(option))
			{
				usefulOptionParser.printErrorMessageAndHelpThenExit("Please specify the option --%1$s with an argument", option);
				throw newImpossibleException();
			}
		}
		else
		{
			if (option.defaultValues().isEmpty())
			{
				usefulOptionParser.printErrorMessageAndHelpThenExit("Please specify the option --%1$s", option);
				throw newImpossibleException();
			}
		}

		final V optionValue;
		try
		{
			optionValue = optionSet.valueOf(option);
		}
		catch (final OptionException ignored)
		{
			usefulOptionParser.printErrorMessageAndHelpThenExit("Please specify the option --%1$s with only one argument", option);
			throw newImpossibleException();
		}

		// This assertion is valid because we have checked hasArgument() earlier
		assert optionValue != null;
		return optionValue;
	}

}
