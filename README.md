# Project-Env CLI

![Build](https://github.com/Project-Env/project-env-cli/workflows/Build/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Project-Env_project-env-cli&metric=alert_status)](https://sonarcloud.io/dashboard?id=Project-Env_project-env-cli)

The Project-Env CLI represents the raw core of Project-Env which allows the setup of tools based on a `project-env.toml` configuration file and therefore the integration of Project-Env into an existing environment (e.g IDE). See [Project-Env](https://project-env.github.io/) for more details.

## Configuration file

```toml
# Directory where the tools should be installed.
# Must located in the project directory.
tools_directory = "<path>"

[jdk]
# Distribution version according to the Disco API.
# See https://api.foojay.io/disco/v2.0/distributions?include_synonyms=false
version = "<version>"
# Distribution name according to the Disco API.
# See https://api.foojay.io/disco/v2.0/distributions?include_versions=false
distribution = "<name>"
# [optional]
# Arbitrary commands which should be executed after extracting.
post_extraction_commands = [
    "<command>",
]

[maven]
# The Maven version.
# See https://archive.apache.org/dist/maven/binaries
version = "<version>"
# [optional]
# The project-relative path to a Maven settings file which should be used as a global settings file.
# Note that the parameter is ignored, in case the file cannot not be found.
global_settings_file = "<path>"
# [optional]
# The project-relative path to a Maven settings file which should be used as a user settings file. 
# Note that the parameter is ignored, in case the file cannot not be found.
user_settings_file = "<path>"
# [optional]
# Arbitrary commands which should be executed after extracting.
post_extraction_commands = [
    "<command>",
]

[gradle]
# The Gradle version.
# See https://gradle.org/releases
version = "<version>"
# [optional]
# Arbitrary commands which should be executed after extracting.
post_extraction_commands = [
    "<command>",
]

[nodejs]
# The NodeJS version.
# See https://nodejs.org/download/release
version = "<version>"
# [optional]
# Arbitrary commands which should be executed after extracting.
post_extraction_commands = [
    "<command>",
]

[groovy]
# The Groovy version.
# See https://groovy.jfrog.io/artifactory/dist-release-local/groovy-zips
version = "<version>"
# [optional]
# Arbitrary commands which should be executed after extracting.
post_extraction_commands = [
    "<command>",
]

[git]
# A directory with Git hooks which should be copied into the '.git/hooks' directory.
hooks_directory = "<path>"

[[generic]]
# [optional], if 'download_urls' is configured
# The URL to download the tool from.
download_url = "<url>"
# [optional], if 'download_url' is configured
# OS specific download URL's (they have precedence over the non-OS download URL).
# Valid values for os are 'macos', 'windows' and 'linux'.
download_urls = [
    { target_os = "<os>", download_url = "<url>" },
]
# [optional]
# The main executable of the tool. 
# The executable is resolved according the configured path elements.
primary_executable = "<path>"
# [optional]
# A map of environment variables, which need to be exposed. 
# All values are resolved according the tool distribution root.
environment_variables = { "<key>" = "<value>" }
# [optional]
# A list of path elements, which needs to be added to the PATH variable. 
# All values are resolved according the tool distribution root.
path_elements = ["<path>"]
# [optional]
# Arbitrary commands which should be executed after extracting.
post_extraction_commands = [
    "<command>",
]
```

### Examples

See [project-env.toml](./code/cli/src/test/resources/io/projectenv/core/cli/integration/project-env.toml) used for integration testing.

### Usage of environment variables

If needed, environment variables can be injected by using the syntax `${<env>}`.

Special case username: As the name of the environment variable containing the username is OS specific, both `USERNAME` and `USER` can be used to inject the username. If `USERNAME` is used, but no environment variable with that name can be found, the value of the variable `USER` is used. This works in the opposite direction too.

## Installation

### Homebrew

`brew install --cask project-env/tap/project-env-cli`
