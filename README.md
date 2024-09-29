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
# Distribution version according the tools index.
# See https://raw.githubusercontent.com/Project-Env/project-env-tools/main/index.json
version = "<version>"
# Distribution name according the tools index (usage of synonym possible too)
# See https://raw.githubusercontent.com/Project-Env/project-env-tools/main/index.json
distribution = "<name>"
# [optional]
# Arbitrary commands which should be executed after extracting.
post_extraction_commands = [
    "<command>",
]

[maven]
# The Maven version.
# See https://raw.githubusercontent.com/Project-Env/project-env-tools/main/index.json
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
# See https://raw.githubusercontent.com/Project-Env/project-env-tools/main/index.json
version = "<version>"
# [optional]
# Arbitrary commands which should be executed after extracting.
post_extraction_commands = [
    "<command>",
]

[nodejs]
# The NodeJS version.
# See https://raw.githubusercontent.com/Project-Env/project-env-tools/main/index.json
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

See [project-env.toml](https://github.com/Project-Env/project-env-cli/blob/main/code/cli/src/test/resources/io/projectenv/core/cli/integration/project-env.toml) used for integration testing.

### Usage of environment variables

If needed, environment variables can be injected by using the syntax `${<env>}`.

Special case username: As the name of the environment variable containing the username is OS specific, both `USERNAME` and `USER` can be used to inject the username. If `USERNAME` is used, but no environment variable with that name can be found, the value of the variable `USER` is used. This works in the opposite direction too.

## Commands

### `install`
Installs the tools as configured in the `project-env.toml` configuration file. If no command has been specified, the `install` command will be executed.

### `upgrade`
Upgrades the tool versions in the `project-env.toml` configuration file either as specified with the `--force-scope` option or according to the `version` property upgrade scope flag. 

The following upgrade scope flags are supported:
* `*`: Upgrade tool version to latest version including major, minor and patch versions
* `^`: Upgrade tool version to latest version including minor and patch versions
* `~`: Upgrade tool version to latest version including patch versions

Note that the flag needs to be added as prefix (e.g. `version = "^11.0.16.1+1"`) and is not available for generic tools or tools without any `version` property.

After running the `upgrade` command, the `install` command needs to be executed again to install the upgraded tools.

## Custom index
The `PROJECT_ENV_TOOL_INDEX` env var can be used to specify a custom index URL. Note that the custom index needs to follow the format of the V2 format (see https://github.com/Project-Env/project-env-tools/blob/main/index-v2.json).

## Cache
All downloaded tool archives are cached, so they don't need to be downloaded multiple times.
* Windows: `C:\Users\<username>\AppData\Local\Project-Env\Cache\Downloads\<archive>`
* macOS: `/Users/<username>/Library/Caches/Project-Env/Downloads/<archive>`
* Linux: `/home/<username>/.cache/project-env/downloads/<archive>`

## Installation

### Homebrew

`brew install --cask project-env/tap/project-env-cli`

### Winget

`winget install ProjectEnv.ProjectEnvCli`

