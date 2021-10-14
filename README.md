# Project-Env CLI

![Build](https://github.com/Project-Env/project-env-cli/workflows/Build/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Project-Env_project-env-cli&metric=alert_status)](https://sonarcloud.io/dashboard?id=Project-Env_project-env-cli)

The Project-Env CLI represents the raw core of Project-Env which allows the setup of tools based on a `project-env.toml` configuration file and therefore the integration of Project-Env into an existing environment (e.g IDE). See https://project-env.io for more details.

## Configuration file

### Usage of environment variables

If needed, environment variables can be injected by using the syntax `${<env>}`.

Special case username: As the name of the environment variable containing the username is OS specific, both `USERNAME` and `USER` can be used to inject the username. If `USERNAME` is used, but no environment variable with that name can be found, the value of the variable `USER` is used. This works in the opposite direction too.

### Global options

The following options are tool dependent:

Option | Mandatory | Description | Valid values
--- | --- | --- | ---
`tools_directory` | Yes | Directory where the tools should be installed. | Any valid directory path, located in the project root.

### JDK options

If you want to install a JDK, you need to add a `[jdk]` section to the `project-env.toml` file and configure it with the following options:

Option | Mandatory | Description | Valid values
--- | --- | --- | ---
`distribution` | Yes | The name of the distribution. | All distributions supported by the [Disco API](https://github.com/foojayio/discoapi) are valid.
`distribution_version` | Yes | The distribution version. | Depends on the used distribution.
`post_extraction_commands` |  | Arbitrary commands which should be executed after extracting the distribution. |

#### Examples

##### Temurin 11

```
[jdk]
distribution = "Temurin"
distribution_version = "11.0.12+7"
```

##### GraalVM 11 with native image

```
[jdk]
distribution = "GraalVM CE 11"
distribution_version = "20.3.0"
post_extraction_commands = [
    "gu install native-image"
]
```

### Maven options

If you want to install Maven, you need to add a `[maven]` section to the `project-env.toml` file and configure it with the following options:

Option | Mandatory | Description | Valid values
--- | --- | --- | ---
`version` | Yes | The Maven version. | See [Maven releases](https://archive.apache.org/dist/maven/binaries) for the valid versions.
`global_settings_file` |  | The project-relative path to a Maven settings file which should be used as a global settings file. Note that the parameter is ignored, in case the file cannot not be found. |
`user_settings_file` |  | The project-relative path to a Maven settings file which should be used as a user settings file. Note that the parameter is ignored, in case the file cannot not be found. |
`post_extraction_commands` |  | Arbitrary commands which should be executed after extracting the distribution. |

#### Examples

##### Maven with global and user specific settings file

```
[maven]
version = "3.6.3"
global_settings_file = "settings.xml"
user_settings_file = "settings-${USER}.xml"
```

### Gradle options

If you want to install Gradle, you need to add a `[gradle]` section to the `project-env.toml` file and configure it with the following options:

Option | Mandatory | Description | Valid values
--- | --- | --- | ---
`version` | Yes | The Gradle version. | See [Gradle releases](https://gradle.org/releases/) for the valid versions.
`post_extraction_commands` |  | Arbitrary commands which should be executed after extracting the distribution. |

#### Examples

##### Simple Gradle

```
[gradle]
version = "6.7.1"
```

### NodeJS options

If you want to install Gradle, you need to add a `[nodejs]` section to the `project-env.toml` file and configure it with the following options:

Option | Mandatory | Description | Valid values
--- | --- | --- | ---
`version` | Yes | The NodeJS version. | See [NodeJS releases](https://nodejs.org/download/release/) for the valid versions.
`post_extraction_commands` |  | Arbitrary commands which should be executed after extracting the distribution. |

#### Examples

##### NodeJS with installed yarn

```
[nodejs]
version = "14.15.3"
post_extraction_commands = ["npm install -g yarn"]
```

### Git options

Git itself cannot be installed, but nevertheless, there are some options which can be defined in a `[git]` section in the `project-env.toml` file:

Option | Mandatory | Description | Valid values
--- | --- | --- | ---
`hooks_directory` |  | A directory with Git hooks which should be copied into the `.git/hooks` directory. |

#### Examples

##### Git with hooks

```
[git]
hooks_directory = "hooks"
```

### Generic tool options

If the needed tool is not supported, it can be installed through a `[generic]` section. The only requirement for the tool is that it needs to be downloadable as `.zip` or `.tar.gz` (or `.tgz`) file. If multiple generic tools should be installed, the sections can be defined as `[[generic]]`.

Option | Mandatory | Description | Valid values
--- | --- | --- | ---
`download_url` or `download_urls` | Yes | The download URL can either be defined with the property `download_url`, if the distribution is OS independent or with the property `download_urls`, if URL's per OS exist. See examples for more details. |
`primary_executable` |  | The main executable of the tool. The executable is resolved according the configured path elements. |
`environment_variables` |  | A map of environment variables, which need to be exposed. All values are resolved according the tool distribution root. |
`path_elements` |  | A list of path elements, which needs to be added to the `PATH` variable. All values are resolved according the tool distribution root. |
`post_extraction_commands` |  | Arbitrary commands which should be executed after extracting the distribution. |

#### Examples

##### JAXB-RI

```
[generic]
download_url = "https://repo1.maven.org/maven2/com/sun/xml/bind/jaxb-ri/3.0.0/jaxb-ri-3.0.0.zip"
environment_variables = { JAXB_HOME = "/" }
path_elements = ["bin"]
```

##### MongoDB Database Tools

```
[generic]
download_urls = [
    { target_os = "MACOS", download_url = "https://fastdl.mongodb.org/tools/db/mongodb-database-tools-macos-x86_64-100.4.1.zip" },
    { target_os = "WINDOWS", download_url = "https://fastdl.mongodb.org/tools/db/mongodb-database-tools-windows-x86_64-100.4.1.zip" },
    { target_os = "LINUX", download_url = "https://fastdl.mongodb.org/tools/db/mongodb-database-tools-ubuntu2004-x86_64-100.4.1.tgz" }
]
path_elements = ["bin"]
```

## Installation

### Homebrew

`brew install --cask project-env/tap/project-env-cli`