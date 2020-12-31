#!/bin/bash

SCRIPTPATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

$SCRIPTPATH/project-env-shell project-env.xml

source .project-env
zsh -i