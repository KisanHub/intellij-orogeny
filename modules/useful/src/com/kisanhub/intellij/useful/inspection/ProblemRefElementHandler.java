/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.inspection;

import com.intellij.codeInspection.reference.RefElement;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("InterfaceWithOnlyOneDirectInheritor")
public interface ProblemRefElementHandler
{
	@SuppressWarnings("MethodWithTooManyParameters")
	void handleProblemRefElement(@NotNull final RefElement refElement, final boolean filterSuppressed, @Nullable final PsiElement psiElement, @NotNull final HighlightSeverity severity, @NotNull final String inspectionToolName, @NotNull final String descriptionMessage, final int lineNumber);
}
