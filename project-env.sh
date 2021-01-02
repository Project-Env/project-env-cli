#!/bin/bash

SCRIPTPATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

$SCRIPTPATH/project-env-cli \
  --config-file=project-env.xml \
  --output-mode=INTERACTIVE_SHELL \
  --output-file=.project-env

source .project-env
zsh -i