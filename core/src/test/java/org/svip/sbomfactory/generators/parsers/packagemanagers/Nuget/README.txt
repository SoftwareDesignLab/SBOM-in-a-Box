 * There seem to be exactly eight possible cases:
 * 1. no dependencies nor frameworkAssemblies (NugetParserNoDependenciesTest) [DONE]
 * 2. one dependency (NugetParserErrLogIOTest) [DONE]
 * 3. one frameworkAssembly (NugetParserOneFrameworkAssemblyTest)
 * 4. dependencies (NugetParserDependenciesTest)
 * 5. frameworkAssemblies (NugetParserFrameworkAssembliesTest) [DONE]
 * 6. dependencies AND frameworkAssemblies (NugetParserDependenciesAndFrameworksTest) [DONE]
 * 7. dependency group(s) [RARE] (NugetParserDependencyGroupsTest)
 * 8. dependency group(s) + frameworkAssemblie(s) [VERY RARE] (NugetParserDependencyGroupsAndFrameworksTest)
 *
 * //todo instead of separate modules as tests, go into Dylan's code and refactor ParserDepFileTestCore
 * in such a way that we can combine all eight