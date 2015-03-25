/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders;

import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.*;

import static com.intellij.openapi.compiler.CompilerMessageCategory.ERROR;
import static com.intellij.openapi.compiler.CompilerMessageCategory.values;
import static java.lang.String.format;
import static java.lang.System.err;
import static java.lang.System.exit;
import static java.util.Locale.ENGLISH;

public final class CategorisedProjectValidationMessagesRecorder implements ProjectValidationMessagesRecorder
{
	@NotNull
	private static final CompilerMessageCategory[] AllCompilerMessageCategories = allCompilerMessageCategories();

	@NotNull
	private static CompilerMessageCategory[] allCompilerMessageCategories()
	{
		final CompilerMessageCategory[] values = values();
		assert values != null;
		return values;
	}

	@NotNull
	private final Map<CompilerMessageCategory, Set<String>> recordsCategorised;

	public CategorisedProjectValidationMessagesRecorder()
	{
		recordsCategorised = new EnumMap<CompilerMessageCategory, Set<String>>(CompilerMessageCategory.class);
		for (final CompilerMessageCategory value : allCompilerMessageCategories())
		{
			recordsCategorised.put(value, new LinkedHashSet<String>(1000));
		}
	}

	@Override
	public void record(@NotNull final Project project, @NotNull final CompilerMessageCategory compilerMessageCategory, @NonNls @NotNull final String message)
	{
		final String format = format(ENGLISH, "%1$s:%2$s:%3$s", project.getName(), compilerMessageCategory.name(), message);//NON-NLS
		final Set<String> strings = getMessages(compilerMessageCategory);
		strings.add(format);
	}

	public void recordToPrintStream(@NotNull final PrintStream printStream)
	{
		for (final CompilerMessageCategory value : AllCompilerMessageCategories)
		{
			final Set<String> messages = getMessages(value);
			for (final String message : messages)
			{
				printStream.println(message);
			}
		}
	}

	@NotNull
	private Set<String> getMessages(@NotNull final CompilerMessageCategory compilerMessageCategory)
	{
		final Set<String> strings = recordsCategorised.get(compilerMessageCategory);
		assert strings != null;
		return strings;
	}
}
