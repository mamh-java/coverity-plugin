/*******************************************************************************
 * Copyright (c) 2017 Synopsys, Inc
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Synopsys, Inc - initial implementation and documentation
 *******************************************************************************/
package jenkins.plugins.coverity;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Util;

public class InvocationAssistance {

    // deprecated fields which were removed in plugin version 1.9
    private transient boolean isUsingMisra;
    private transient String misraConfigFile;
    private transient boolean isUsingPostCovBuildCmd;
    private transient String postCovBuildCmd;
    private transient boolean isUsingPostCovAnalyzeCmd;
    private transient String postCovAnalyzeCmd;

    // deprecated fields which were removed in plugin version 1.10
    private transient String saOverride;

    private final String buildArguments;
    private final String analyzeArguments;
    private final String commitArguments;
    private List<String> javaWarFilesNames;
    private final List<JavaWarFile> javaWarFiles;
    private final boolean csharpMsvsca;
    private ToolsOverride toolsOverride;
    private MisraConfig misraConfig;
    private final boolean isScriptSrc;
    private PostCovBuild postCovBuild;
    private PostCovAnalyze postCovAnalyze;

    /**
     * Absolute path to the intermediate directory that Coverity should use. Null to use the default.
     */
    private final String intermediateDir;

    public boolean getUseAdvancedParser() {
        return useAdvancedParser;
    }

    private final boolean useAdvancedParser;

    @DataBoundConstructor
    public InvocationAssistance(PostCovBuild postCovBuild,
                                PostCovAnalyze postCovAnalyze,
                                boolean isScriptSrc,
                                String buildArguments,
                                String analyzeArguments,
                                String commitArguments,
                                String intermediateDir,
                                MisraConfig misraConfig,
                                List<JavaWarFile> javaWarFiles,
                                boolean csharpMsvsca,
                                ToolsOverride toolsOverride,
                                boolean useAdvancedParser) {
        this.postCovBuild = postCovBuild;
        this.postCovAnalyze = postCovAnalyze;
        this.isScriptSrc = isScriptSrc;
        this.buildArguments = Util.fixEmpty(buildArguments);
        this.analyzeArguments = Util.fixEmpty(analyzeArguments);
        this.commitArguments = Util.fixEmpty(commitArguments);
        this.intermediateDir = Util.fixEmpty(intermediateDir);
        this.misraConfig = misraConfig;
        List<String> tempJavaWarFilesPaths = new ArrayList<String>();
        if (javaWarFiles != null && !javaWarFiles.isEmpty()) {
            for (JavaWarFile javaWarFile : javaWarFiles) {
                tempJavaWarFilesPaths.add(javaWarFile.getWarFile());
            }
        }
        this.javaWarFilesNames = tempJavaWarFilesPaths;
        this.javaWarFiles = javaWarFiles;
        this.csharpMsvsca = csharpMsvsca;
        this.toolsOverride = toolsOverride;
        this.useAdvancedParser = useAdvancedParser;
    }

    /**
     * Implement readResolve to update the de-serialized object in the case transient data was found. Transient fields
     * will be read during de-serialization and readResolve allow updating the InvocationAssistance object after being created.
     */
    protected Object readResolve() {
        // Check for existing misraConfig
        if (isUsingMisra && misraConfigFile != null) {
            this.misraConfig = new MisraConfig(misraConfigFile);
        }

        // Check for existing postCovBuild
        if (isUsingPostCovBuildCmd && postCovBuildCmd != null) {
            this.postCovBuild = new PostCovBuild(postCovBuildCmd);
        }

        // Check for existing postCovAnalyze
        if (isUsingPostCovAnalyzeCmd && postCovAnalyzeCmd != null) {
            this.postCovAnalyze = new PostCovAnalyze(postCovAnalyzeCmd);
        }

        // Check for existing saOverride
        if (saOverride != null) {
            toolsOverride = new ToolsOverride(null);
            toolsOverride.setToolsLocation(saOverride);
            saOverride = null;
        }

        return this;
    }

    public String getPostCovBuildCmd() {
        return postCovBuild != null ? postCovBuild.getPostCovBuildCmd() : null;
    }

    public boolean getIsUsingPostCovBuildCmd() {
        return postCovBuild != null;
    }

    public boolean getIsUsingPostCovAnalyzeCmd() {
        return postCovAnalyze != null;
    }

    public String getPostCovAnalyzeCmd() {
        return postCovAnalyze != null ? postCovAnalyze.getPostCovAnalyzeCmd() : null;
    }

    public boolean getIsScriptSrc() {
        return isScriptSrc;
    }

    public String getBuildArguments() {
        return buildArguments;
    }

    public String getAnalyzeArguments() {
        return analyzeArguments;
    }

    public String getCommitArguments() {
        return commitArguments;
    }

    public String getIntermediateDir() {
        return intermediateDir;
    }

    public List<JavaWarFile> getJavaWarFiles() {
        return javaWarFiles;
    }

    public List<String> getJavaWarFilesNames() {
        return javaWarFilesNames;
    }

    public boolean getCsharpMsvsca() {
        return csharpMsvsca;
    }

    public ToolsOverride getToolsOverride() {
        return toolsOverride;
    }

    public boolean getIsUsingMisra() {
        return misraConfig != null;
    }

    public String getMisraConfigFile() {
        return misraConfig != null ? misraConfig.getMisraConfigFile() : null;
    }

    /**
     * For each variable in override, use that value, otherwise, use the value in this object.
     *
     * @param override source
     * @return a new, merged InvocationAssistance
     */
    public InvocationAssistance merge(InvocationAssistance override) {
        String analyzeArguments = override.getAnalyzeArguments() != null ? override.getAnalyzeArguments() : getAnalyzeArguments();
        String buildArguments = override.getBuildArguments() != null ? override.getBuildArguments() : getBuildArguments();
        String commitArguments = override.getCommitArguments() != null ? override.getCommitArguments() : getCommitArguments();
        String intermediateDir = override.getIntermediateDir() != null ? override.getIntermediateDir() : getIntermediateDir();
        boolean csharpMsvsca = override.getCsharpMsvsca();
        ToolsOverride toolsOverrideOverride;
        if (override.saOverride!= null) {
            toolsOverrideOverride = new ToolsOverride(null);
            toolsOverrideOverride.setToolsLocation(override.saOverride);
        } else {
            toolsOverrideOverride = this.toolsOverride;
        }
        MisraConfig misraConfig = override.isUsingMisra ? new MisraConfig(override.misraConfigFile) : null;
        boolean isScriptSrc = override.getIsScriptSrc();
        PostCovBuild postBuild = override.isUsingPostCovBuildCmd ? new PostCovBuild(override.postCovBuildCmd) : null;
        PostCovAnalyze postCovAnalyze = override.isUsingPostCovAnalyzeCmd ? new PostCovAnalyze(override.postCovAnalyzeCmd) : null;
        List<JavaWarFile> javaWarFiles = override.getJavaWarFiles();
        boolean useAdvancedParser = override.getUseAdvancedParser();
        return new InvocationAssistance(
            postBuild,
            postCovAnalyze,
            isScriptSrc,
            buildArguments,
            analyzeArguments,
            commitArguments,
            intermediateDir,
            misraConfig,
            javaWarFiles,
            csharpMsvsca,
            toolsOverrideOverride,
            useAdvancedParser);
    }

    public String checkIAConfig() {
        boolean delim = true;
        String errorText = "Errors with your \"Perform Coverity build/analyze/commit\" options: \n";
        // Making sure they specified misra config
        if (getIsUsingMisra()) {
            if (getMisraConfigFile() == null) {
                delim = false;
            } else if (getMisraConfigFile().isEmpty()) {
                delim = false;
            } else if (getMisraConfigFile().trim().isEmpty()) {
                delim = false;
            }
        }

        if (delim) {
            errorText = "Pass";
        } else {
            errorText += "[Error] No MISRA configuration file was specified. \n";
        }

        return errorText;
    }
}
