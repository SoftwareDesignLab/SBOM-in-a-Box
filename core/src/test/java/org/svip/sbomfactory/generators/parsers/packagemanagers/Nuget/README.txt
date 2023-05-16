 * There seem to be exactly eight possible cases:
 * 1. no dependencies nor frameworkAssemblies (NugetParserNoDependenciesTest)
 * 2. one dependency (NugetParserErrLogIOTest)
 * 3. one frameworkAssembly (NugetParserOneFrameworkAssemblyTest)
 * 4. dependencies (NugetParserDependenciesTest)
 * 5. frameworkAssemblies (NugetParserFrameworkAssembliesTest)
 * 6. dependencies AND frameworkAssemblies (NugetParserDependenciesAndFrameworksTest)
 * 7. dependency group(s) (NugetParserDependencyGroupsTest)
 * 8. dependency group(s) + frameworkAssemblie(s) (NugetParserDependencyGroupsAndFrameworksTest)
 *
 * //todo instead of separate modules as tests, go into Dylan's code and refactor ParserDepFileTestCore
 * in such a way that we can combine all eight