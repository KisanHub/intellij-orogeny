/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.rebuilding;

import com.intellij.openapi.ui.TestDialog;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.ui.Messages.NO;

public final class NoTestDialog implements TestDialog
{
	@NotNull
	public static final TestDialog AnswerAlwaysAsNo = new NoTestDialog();

	private NoTestDialog()
	{
	}

	@Override
	public int show(@NotNull final String message)
	{
		return NO;
	}
}
