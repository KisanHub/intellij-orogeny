/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.rebuilding;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileStatusNotification;
import com.intellij.openapi.compiler.CompilerMessage;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.project.Project;
import com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.ProjectValidationMessagesRecorder;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.compiler.CompilerMessageCategory.ERROR;
import static com.intellij.openapi.compiler.CompilerMessageCategory.STATISTICS;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public class RebuildCompileStatusNotification implements CompileStatusNotification
{
	@NotNull
	private final ProjectValidationMessagesRecorder projectValidationMessagesRecorder;

	@NotNull
	private final Project project;

	public RebuildCompileStatusNotification(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder, @NotNull final Project project)
	{
		this.projectValidationMessagesRecorder = projectValidationMessagesRecorder;
		this.project = project;
	}

	@Override
	public void finished(final boolean aborted, final int errors, final int warnings, @NotNull final CompileContext compileContext)
	{
		if (aborted)
		{
			throw new IllegalStateException("It should not be possible to abort a offlineRebuild when headless");
		}

		assert STATISTICS != null;
		final String format1 = format(ENGLISH, "There are %1$s error%2$s", errors, errors == 1 ? "s" : ""); //NON-NLS
		assert format1 != null;
		projectValidationMessagesRecorder.record(project, STATISTICS, format1);

		final String format2 = format(ENGLISH, "There are %1$s warning%2$s", warnings, warnings == 1 ? "s" : ""); //NON-NLS
		assert format2 != null;
		projectValidationMessagesRecorder.record(project, STATISTICS, format2);

		final CompilerMessageCategory[] compilerMessageCategories = CompilerMessageCategory.values();
		assert compilerMessageCategories != null;
		for (final CompilerMessageCategory compilerMessageCategory : compilerMessageCategories)
		{
			final CompilerMessage[] messages = compileContext.getMessages(ERROR);
			assert messages != null;
			for (final CompilerMessage message : messages)
			{
				final String format3 = format(ENGLISH, "File '%1$s' '%2$s'", message.getVirtualFile(), message.getMessage()); // NON-NLS
				assert format3 != null;
				projectValidationMessagesRecorder.record(project, compilerMessageCategory, format3);
			}
		}
	}
}
