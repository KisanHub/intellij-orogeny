/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.validation;

import com.intellij.codeInspection.reference.RefElement;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPsiElementPointer;
import com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.ProjectValidationMessagesRecorder;
import com.kisanhub.intellij.useful.inspection.ProblemRefElementHandler;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.openapi.compiler.CompilerMessageCategory.*;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class ValidatingProblemRefElementHandler implements ProblemRefElementHandler
{
	@NotNull
	private final ProjectValidationMessagesRecorder projectValidationMessagesRecorder;

	public ValidatingProblemRefElementHandler(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder)
	{
		this.projectValidationMessagesRecorder = projectValidationMessagesRecorder;
	}

	@SuppressWarnings({"IfMayBeConditional", "MethodWithTooManyParameters"})
	@Override
	public void handleProblemRefElement(@NotNull final RefElement refElement, final boolean filterSuppressed, @Nullable final PsiElement psiElement, @NotNull final HighlightSeverity severity, @NotNull final String inspectionToolName, @NotNull final String descriptionMessage, final int lineNumber)
	{
		final CompilerMessageCategory compilerMessageCategory;
		if (severity.compareTo(HighlightSeverity.WEAK_WARNING) <= 0)
		{
			compilerMessageCategory = INFORMATION;
		}
		else if (severity.compareTo(HighlightSeverity.WARNING) <= 0)
		{
			compilerMessageCategory = WARNING;
		}
		else
		{
			compilerMessageCategory = ERROR;
		}
		assert compilerMessageCategory != null;


		@NonNls final String filePath = filePath(refElement);

		final String format = format(ENGLISH, "%1$s %2$s%3$s", descriptionMessage, filePath, lineNumber > -1 ? ":" + lineNumber : ""); //NON-NLS
		assert format != null;
		projectValidationMessagesRecorder.record(compilerMessageCategory, inspectionToolName, format);
	}

	@NotNull
	@NonNls
	private static String filePath(@NotNull final RefElement refElement)
	{
		final SmartPsiElementPointer<?> pointer = getSmartPsiElementPointer(refElement);

		@Nullable final PsiFile psiFile = pointer.getContainingFile();
		if (psiFile == null)
		{
			return "(file)";
		}
		else
		{
			@Nullable final VirtualFile virtualFile = psiFile.getVirtualFile();
			if (virtualFile == null)
			{
				return "(memory)";
			}
			else
			{
				final String filePath = virtualFile.getCanonicalPath();
				assert filePath != null;
				return filePath;
			}
		}
	}

	@NotNull
	private static SmartPsiElementPointer<?> getSmartPsiElementPointer(@NotNull final RefElement refElement)
	{
		final SmartPsiElementPointer<?> pointer = refElement.getPointer();
		assert pointer != null;
		return pointer;
	}
}
