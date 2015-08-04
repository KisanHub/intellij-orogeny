/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.external;

import com.kisanhub.intellij.useful.commandLine.external.UsefulEntryPoint;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UtilityClass")
public final class EntryPoint
{
	public static void main(@NotNull final String... commandLineArguments)
	{
		new UsefulEntryPoint("com.kisanhub.intellij.orogeny.plugin.BuilderCommandLineApplicationStarterEx").execute(commandLineArguments);
	}
	
	private EntryPoint()
	{
	}

}
