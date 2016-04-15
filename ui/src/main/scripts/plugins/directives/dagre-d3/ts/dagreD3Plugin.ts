///
/// Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
/// and other contributors as indicated by the @author tags.
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///    http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

/// <reference path="../../../includes.ts"/>
/// <reference path="dagreD3Globals.ts"/>
/// <reference path="dagreD3Directive.ts"/>
module DagreD3 {
  _module.directive('dagreD3', ['$compile', ($compile) => {
    return new DagreD3.DagreD3Directive($compile);
  }]);

  hawtioPluginLoader.addModule(pluginName);
}
