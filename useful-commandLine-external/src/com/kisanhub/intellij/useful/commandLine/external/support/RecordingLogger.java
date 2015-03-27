/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.commandLine.external.support;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.locks.Lock;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class RecordingLogger extends Logger
{
	@NotNull
	@NonNls
	private static final String debug = "debug";

	@NotNull
	@NonNls
	private static final String info = "info";

	@NotNull
	@NonNls
	private static final String warn = "warn";

	@NotNull
	@NonNls
	private static final String error = "error";

	@NotNull
	private final String category;

	@NotNull
	private final List<String> messages;

	@NotNull
	private final Lock writeLock;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public RecordingLogger(@NotNull final String category, @NotNull final List<String> messages, @NotNull final Lock writeLock)
	{
		this.category = category;
		this.messages = messages;
		this.writeLock = writeLock;
	}

	@Override
	public boolean isDebugEnabled()
	{
		return false;
	}

	@Override
	public void debug(@NonNls @Nullable final String message)
	{
		addMessage(debug, message);
	}

	@Override
	public void debug(@Nullable final Throwable t)
	{
		addMessage(debug, null, t);
	}

	@Override
	public void debug(@NonNls @Nullable final String message, @Nullable final Throwable t)
	{
		addMessage(debug, message, t);
	}

	@Override
	public void info(@NonNls @Nullable final String message)
	{
		addMessage(info, message);
	}

	@Override
	public void info(@NonNls @Nullable final String message, @Nullable final Throwable t)
	{
		addMessage(info, message, t);
	}

	@Override
	public void warn(@NonNls @Nullable final String message, @Nullable final Throwable t)
	{
		addMessage(warn, message, t);
	}

	@Override
	public void error(@NonNls @Nullable final String message, @Nullable final Throwable t, @NonNls @NotNull final String... details)
	{
		addMessage(error, message, t);
	}

	@Override
	public void setLevel(@Nullable final Level level)
	{
	}

	private void addMessage(@NotNull @NonNls final String level, @NonNls @Nullable final String message, @Nullable final Throwable t)
	{
		if (t == null)
		{
			final String format = format(ENGLISH, "%1$s - (null)", ifNullMessage(message));
			assert format != null;
			addMessage(level, format);
		}
		else
		{
			final Class<? extends Throwable> clazz = t.getClass();
			assert clazz != null;
			final String format = format(ENGLISH, "%1$s - %2$s ('%3$s')", ifNullMessage(message), t.getMessage(), clazz.getName());
			assert format != null;
			addMessage(level, format);
		}
	}

	private boolean addMessage(@NonNls @NotNull final String level, @NonNls @Nullable final String message)
	{
		writeLock.lock();
		try
		{
			return messages.add(format(ENGLISH, "level %1$s category %2$s: '%3$s'", category, level, ifNullMessage(message)));
		}
		finally
		{
			writeLock.unlock();
		}
	}

	@NonNls
	@NotNull
	private static String ifNullMessage(@Nullable @NonNls final String message)
	{
		return message == null ? "(null)" : message;
	}
}
