/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin;

import com.kisanhub.intellij.useful.commandLine.commandLineApplicationStarterExs.AbstractUsefulProjectUsingCommandLineApplicationStarterEx;

@SuppressWarnings("UnusedDeclaration")
public final class BuilderCommandLineApplicationStarterEx extends AbstractUsefulProjectUsingCommandLineApplicationStarterEx
{
	public BuilderCommandLineApplicationStarterEx()
	{
		super(new BuilderUsefulProjectUsingExecutor());
	}
}
