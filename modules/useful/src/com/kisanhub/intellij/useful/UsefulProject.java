/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.InspectionProfile;
import com.intellij.codeInspection.ex.GlobalInspectionContextImpl;
import com.intellij.codeInspection.ex.InspectionManagerEx;
import com.intellij.compiler.CompilerConfiguration;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModulePointerManager;
import com.intellij.openapi.project.ex.ProjectEx;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.RootPolicy;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.packaging.artifacts.ArtifactManager;
import com.intellij.profile.codeInspection.InspectionProjectProfileManager;
import com.intellij.psi.PsiManager;
import com.intellij.ui.content.ContentManager;
import com.kisanhub.intellij.useful.inspection.ProblemRefElementHandler;
import com.kisanhub.intellij.useful.inspection.UsefulGlobalInspectionContextImpl;
import com.kisanhub.intellij.useful.inspection.inspectionToolPresentationFactories.UsefulInspectionToolPresentationFactory;
import com.kisanhub.intellij.useful.moduleOrderEntries.ProcessModuleOrder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.System.getProperty;

@SuppressWarnings({"ClassNamePrefixedWithPackageName", "ClassWithTooManyFields"})
public final class UsefulProject implements ProcessModuleOrder
{
	@SuppressWarnings("PublicField")
	@NotNull
	public final ProjectEx project;

	@SuppressWarnings("PublicField")
	@NotNull
	public final ProjectRootManager projectRootManager;

	@SuppressWarnings("PublicField")
	@NotNull
	public final ArtifactManager artifactManager;

	@SuppressWarnings("PublicField")
	@NotNull
	public final ModuleManager moduleManager;

	@SuppressWarnings("PublicField")
	@NotNull
	public final ModulePointerManager modulePointerManager;

	@SuppressWarnings("PublicField")
	@NotNull
	public final PsiManager psiManager;

	@SuppressWarnings("PublicField")
	@NotNull
	public final PathMacroManager pathMacroManager;

	@SuppressWarnings("PublicField")
	@NotNull
	public final CompilerManager compilerManager;

	@SuppressWarnings("PublicField")
	@NotNull
	public final CompilerConfiguration compilerConfiguration;

	@SuppressWarnings("PublicField")
	@NotNull
	public final InspectionManagerEx inspectionManagerEx;

	@SuppressWarnings("PublicField")
	@NotNull
	public final InspectionProjectProfileManager inspectionProjectProfileManager;

	@SuppressWarnings("PublicField")
	@NotNull
	public final InspectionProfile inspectionProfile;

	@SuppressWarnings("PublicField")
	@NotNull
	public final AnalysisScope analysisScope;

	// project may be ProjectEx
	public UsefulProject(@NotNull final ProjectEx project)
	{
		this.project = project;
		projectRootManager = projectRootManager();
		artifactManager = artifactManager();
		moduleManager = moduleManager();
		modulePointerManager = modulePointerManager();
		psiManager = psiManager();
		pathMacroManager = pathMacroManager();
		compilerManager = compilerManager();
		compilerConfiguration = compilerConfiguration();
		inspectionManagerEx = inspectionManagerEx();
		inspectionProjectProfileManager = inspectionProjectProfileManager();

		inspectionProfile = inspectionProjectProfileOrRootProfile();

		// This is done by IntelliJ's InspectionApplication logic
		inspectionManagerEx.setProfile(inspectionProfile.getName());

		analysisScope = new AnalysisScope(project);
	}

	@Override
	public <R> void useModuleOrderEntriesInModuleDependencyOrder(@NotNull final RootPolicy<R> rootPolicy, @Nullable final R initialValue)
	{
		for (final ProcessModuleOrder processModuleOrder : usefulModulesSortedInDependencyOrder())
		{
			processModuleOrder.useModuleOrderEntriesInModuleDependencyOrder(rootPolicy, initialValue);
		}
	}

	@NotNull
	public List<ProcessModuleOrder> usefulModulesSortedInDependencyOrder()
	{
		final Module[] sortedModules = moduleManager.getSortedModules();
		final List<ProcessModuleOrder> usefulModulesSorted = new ArrayList<ProcessModuleOrder>(sortedModules.length);
		for (final Module sortedModule : sortedModules)
		{
			usefulModulesSorted.add(new UsefulModule(sortedModule));
		}
		return usefulModulesSorted;
	}

	public void inspect(@NotNull final ProblemRefElementHandler problemRefElementHandler)
	{
		final NotNullLazyValue<ContentManager> contentManager = inspectionManagerEx.getContentManager();
		assert contentManager != null;

		final Set<GlobalInspectionContextImpl> runningContexts = inspectionManagerEx.getRunningContexts();

		final UsefulInspectionToolPresentationFactory inspectionToolPresentationFactory = new UsefulInspectionToolPresentationFactory(problemRefElementHandler);
		final UsefulGlobalInspectionContextImpl usefulGlobalInspectionContext = new UsefulGlobalInspectionContextImpl(project, contentManager, runningContexts, inspectionProfile, inspectionToolPresentationFactory);
		@SuppressWarnings("AccessOfSystemProperties") final boolean runGlobalToolsOnly = getProperty("idea.no.local.inspections") != null;
		usefulGlobalInspectionContext.inspect(analysisScope, runGlobalToolsOnly);
	}

	@NotNull
	private ProjectRootManager projectRootManager()
	{
		@SuppressWarnings("LocalVariableHidesMemberVariable") final ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
		assert projectRootManager != null;
		return projectRootManager;
	}

	@NotNull
	private ArtifactManager artifactManager()
	{
		@SuppressWarnings("LocalVariableHidesMemberVariable") final ArtifactManager artifactManager = ArtifactManager.getInstance(project);
		assert artifactManager != null;
		return artifactManager;
	}

	@NotNull
	private ModuleManager moduleManager()
	{
		@SuppressWarnings("LocalVariableHidesMemberVariable") final ModuleManager moduleManager = ModuleManager.getInstance(project);
		assert moduleManager != null;
		return moduleManager;
	}

	@NotNull
	private ModulePointerManager modulePointerManager()
	{
		@SuppressWarnings("LocalVariableHidesMemberVariable") final ModulePointerManager modulePointerManager = ModulePointerManager.getInstance(project);
		assert modulePointerManager != null;
		return modulePointerManager;
	}

	@NotNull
	private PsiManager psiManager()
	{
		return PsiManager.getInstance(project);
	}

	@NotNull
	private PathMacroManager pathMacroManager()
	{
		@SuppressWarnings("LocalVariableHidesMemberVariable") final PathMacroManager pathMacroManager = PathMacroManager.getInstance(project);
		assert pathMacroManager != null;
		return pathMacroManager;
	}

	@NotNull
	private CompilerManager compilerManager()
	{
		@SuppressWarnings("LocalVariableHidesMemberVariable") final CompilerManager compilerManager = CompilerManager.getInstance(project);
		assert compilerManager != null;
		return compilerManager;
	}

	@NotNull
	private CompilerConfiguration compilerConfiguration()
	{
		@SuppressWarnings("LocalVariableHidesMemberVariable") final CompilerConfiguration compilerConfiguration = CompilerConfiguration.getInstance(project);
		assert compilerConfiguration != null;
		return compilerConfiguration;
	}

	@NotNull
	private InspectionManagerEx inspectionManagerEx()
	{
		final InspectionManager inspectionManager = InspectionManager.getInstance(project);
		assert inspectionManager != null;
		return (InspectionManagerEx) inspectionManager;
	}

	@NotNull
	private InspectionProjectProfileManager inspectionProjectProfileManager()
	{
		@SuppressWarnings("LocalVariableHidesMemberVariable") final InspectionProjectProfileManager inspectionProjectProfileManager = InspectionProjectProfileManager.getInstance(project);
		assert inspectionProjectProfileManager != null;
		return inspectionProjectProfileManager;
	}

	@NotNull
	private InspectionProfile inspectionProjectProfileOrRootProfile()
	{
		return inspectionProjectProfileManager.getInspectionProfile();
	}
}
