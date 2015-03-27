/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.commandLine.external.support;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.diagnostic.Logger.Factory;
import com.kisanhub.intellij.useful.commandLine.commandLineApplicationStarterExs.UsingExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.intellij.openapi.diagnostic.Logger.setFactory;

@SuppressWarnings("Singleton")
public final class RecordingLoggerFactory implements Factory
{
	@NotNull
	private final List<String> messages;

	@NotNull
	private final Lock readLock;

	@NotNull
	private final Lock writeLock;

	@SuppressWarnings("StaticNonFinalField")
	@Nullable
	private static RecordingLoggerFactory disgustingHackBecauseIntelliJCreatesUsingReflectionAndWeCanNotAccessOurInstanceAnyOtherWay = null;

	@SuppressWarnings("FieldRepeatedlyAccessedInMethod")
	@NotNull
	public static synchronized RecordingLoggerFactory replaceLoggingFactory()
	{
		assert disgustingHackBecauseIntelliJCreatesUsingReflectionAndWeCanNotAccessOurInstanceAnyOtherWay == null;
		setFactory(RecordingLoggerFactory.class);
		try
		{
			return disgustingHackBecauseIntelliJCreatesUsingReflectionAndWeCanNotAccessOurInstanceAnyOtherWay;
		}
		finally
		{
			disgustingHackBecauseIntelliJCreatesUsingReflectionAndWeCanNotAccessOurInstanceAnyOtherWay = null;
		}
	}

	private RecordingLoggerFactory()
	{
		messages = new ArrayList<String>(16);

		final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		readLock = readWriteLock.readLock();
		writeLock = readWriteLock.writeLock();

		captureOurselvesBecauseIntelliJCreatesUsUsingReflection();
	}

	@SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
	private void captureOurselvesBecauseIntelliJCreatesUsUsingReflection()
	{
		disgustingHackBecauseIntelliJCreatesUsingReflectionAndWeCanNotAccessOurInstanceAnyOtherWay = this;
	}

	@Override
	@NotNull
	public Logger getLoggerInstance(@NotNull final String category)
	{
		return new RecordingLogger(category, messages, writeLock);
	}

	public void visitMessages(@NotNull final UsingExecutor<String> messageVisitor)
	{
		readLock.lock();
		try
		{
			for (final String message : messages)
			{
				messageVisitor.use(message);
			}
		}
		finally
		{
			readLock.unlock();
		}
	}

}
