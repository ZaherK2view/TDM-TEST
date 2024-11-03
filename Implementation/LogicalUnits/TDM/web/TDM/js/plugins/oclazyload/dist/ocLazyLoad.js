/**
 * oclazyload - Load modules on demand (lazy load) with angularJS
 * @version v0.5.2
 * @link https://github.com/ocombe/ocLazyLoad
 * @license MIT
 * @author Olivier Combe <olivier.combe@gmail.com>
 */
!function(){"use strict";var regModules=["ng"],initModules=[],regInvokes={},regConfigs=[],justLoaded=[],runBlocks={},ocLazyLoad=angular.module("oc.lazyLoad",["ng"]),broadcast=angular.noop;function moduleExists(moduleName){try{return angular.module(moduleName)}catch(e){if(/No module/.test(e)||e.message.indexOf("$injector:nomod")>-1)return!1}}function invokeQueue(providers,queue,moduleName,reconfig){var i,len,args,provider;if(queue)for(i=0,len=queue.length;i<len;i++)if(args=queue[i],angular.isArray(args)){if(null!==providers){if(!providers.hasOwnProperty(args[0]))throw new Error("unsupported provider "+args[0]);provider=providers[args[0]]}var isNew=registerInvokeList(args,moduleName);if("invoke"!==args[1])isNew&&angular.isDefined(provider)&&provider[args[1]].apply(provider,args[2]);else{var callInvoke=function(fct){var invoked=regConfigs.indexOf(moduleName+"-"+fct);(-1===invoked||reconfig)&&(-1===invoked&&regConfigs.push(moduleName+"-"+fct),angular.isDefined(provider)&&provider[args[1]].apply(provider,args[2]))};if(angular.isFunction(args[2][0]))callInvoke(args[2][0]);else if(angular.isArray(args[2][0]))for(var j=0,jlen=args[2][0].length;j<jlen;j++)angular.isFunction(args[2][0][j])&&callInvoke(args[2][0][j])}}}function registerInvokeList(args,moduleName){var invokeList=args[2][0],type=args[1],newInvoke=!1;angular.isUndefined(regInvokes[moduleName])&&(regInvokes[moduleName]={}),angular.isUndefined(regInvokes[moduleName][type])&&(regInvokes[moduleName][type]=[]);var onInvoke=function(invokeName){newInvoke=!0,regInvokes[moduleName][type].push(invokeName),broadcast("ocLazyLoad.componentLoaded",[moduleName,type,invokeName])};if(angular.isString(invokeList)&&-1===regInvokes[moduleName][type].indexOf(invokeList))onInvoke(invokeList);else{if(!angular.isObject(invokeList))return!1;angular.forEach(invokeList,(function(invoke){angular.isString(invoke)&&-1===regInvokes[moduleName][type].indexOf(invoke)&&onInvoke(invoke)}))}return newInvoke}function getModuleName(module){var moduleName=null;return angular.isString(module)?moduleName=module:angular.isObject(module)&&module.hasOwnProperty("name")&&angular.isString(module.name)&&(moduleName=module.name),moduleName}ocLazyLoad.provider("$ocLazyLoad",["$controllerProvider","$provide","$compileProvider","$filterProvider","$injector","$animateProvider",function($controllerProvider,$provide,$compileProvider,$filterProvider,$injector,$animateProvider){var jsLoader,cssLoader,templatesLoader,modules={},providers={$controllerProvider:$controllerProvider,$compileProvider:$compileProvider,$filterProvider:$filterProvider,$provide:$provide,$injector:$injector,$animateProvider:$animateProvider},anchor=document.getElementsByTagName("head")[0]||document.getElementsByTagName("body")[0],debug=!1,events=!1;!function(element){if(0===initModules.length){var elements=[element],names=["ng:app","ng-app","x-ng-app","data-ng-app"],NG_APP_CLASS_REGEXP=/\sng[:\-]app(:\s*([\w\d_]+);?)?\s/,append=function(elm){return elm&&elements.push(elm)};angular.forEach(names,(function(name){names[name]=!0,append(document.getElementById(name)),name=name.replace(":","\\:"),element[0].querySelectorAll&&(angular.forEach(element[0].querySelectorAll("."+name),append),angular.forEach(element[0].querySelectorAll("."+name+"\\:"),append),angular.forEach(element[0].querySelectorAll("["+name+"]"),append))})),angular.forEach(elements,(function(elm){if(0===initModules.length){var className=" "+element.className+" ",match=NG_APP_CLASS_REGEXP.exec(className);match?initModules.push((match[2]||"").replace(/\s+/g,",")):angular.forEach(elm.attributes,(function(attr){0===initModules.length&&names[attr.name]&&initModules.push(attr.value)}))}}))}if(0===initModules.length)throw"No module found during bootstrap, unable to init ocLazyLoad";angular.forEach(initModules,(function(moduleName){!function addReg(moduleName){if(-1===regModules.indexOf(moduleName)){regModules.push(moduleName);var mainModule=angular.module(moduleName);invokeQueue(null,mainModule._invokeQueue,moduleName),invokeQueue(null,mainModule._configBlocks,moduleName),angular.forEach(mainModule.requires,addReg)}}(moduleName)}))}(angular.element(window.document)),this.$get=["$log","$q","$templateCache","$http","$rootElement","$rootScope","$cacheFactory","$interval",function($log,$q,$templateCache,$http,$rootElement,$rootScope,$cacheFactory,$interval){var instanceInjector,filesCache=$cacheFactory("ocLazyLoad"),useCssLoadPatch=!1;debug||(($log={}).error=angular.noop,$log.warn=angular.noop,$log.info=angular.noop),providers.getInstanceInjector=function(){return instanceInjector||(instanceInjector=$rootElement.data("$injector")||angular.injector())},broadcast=function(eventName,params){events&&$rootScope.$broadcast(eventName,params),debug&&$log.info(eventName,params)};var buildElement=function(type,path,params){var el,loaded,deferred=$q.defer(),cacheBuster=function(url){var dc=(new Date).getTime();return url.indexOf("?")>=0?"&"===url.substring(0,url.length-1)?url+"_dc="+dc:url+"&_dc="+dc:url+"?_dc="+dc};switch(angular.isUndefined(filesCache.get(path))&&filesCache.put(path,deferred.promise),type){case"css":(el=document.createElement("link")).type="text/css",el.rel="stylesheet",el.href=!1===params.cache?cacheBuster(path):path;break;case"js":(el=document.createElement("script")).src=!1===params.cache?cacheBuster(path):path;break;default:deferred.reject(new Error('Requested type "'+type+'" is not known. Could not inject "'+path+'"'))}el.onload=el.onreadystatechange=function(e){el.readyState&&!/^c|loade/.test(el.readyState)||loaded||(el.onload=el.onreadystatechange=null,loaded=1,broadcast("ocLazyLoad.fileLoaded",path),deferred.resolve())},el.onerror=function(e){deferred.reject(new Error("Unable to load "+path))},el.async=params.serie?0:1;var insertBeforeElem=anchor.lastChild;if(params.insertBefore){var element=angular.element(params.insertBefore);element&&element.length>0&&(insertBeforeElem=element[0])}if(anchor.insertBefore(el,insertBeforeElem),"css"==type){var ua=navigator.userAgent.toLowerCase();if(/iP(hone|od|ad)/.test(navigator.platform)){var v=navigator.appVersion.match(/OS (\d+)_(\d+)_?(\d+)?/),iOSVersion=parseFloat([parseInt(v[1],10),parseInt(v[2],10),parseInt(v[3]||0,10)].join("."));useCssLoadPatch=iOSVersion<6}else if(ua.indexOf("android")>-1){var androidVersion=parseFloat(ua.slice(ua.indexOf("android")+8));useCssLoadPatch=androidVersion<4.4}else if(ua.indexOf("safari")>-1&&-1==ua.indexOf("chrome")){var safariVersion=parseFloat(ua.match(/version\/([\.\d]+)/i)[1]);useCssLoadPatch=safariVersion<6}if(useCssLoadPatch)var tries=1e3,interval=$interval((function(){try{el.sheet.cssRules,$interval.cancel(interval),el.onload()}catch(e){--tries<=0&&el.onerror()}}),20)}return deferred.promise};angular.isUndefined(jsLoader)&&((jsLoader=function(paths,callback,params){var promises=[];angular.forEach(paths,(function(path){promises.push(buildElement("js",path,params))})),$q.all(promises).then((function(){callback()}),(function(err){callback(err)}))}).ocLazyLoadLoader=!0),angular.isUndefined(cssLoader)&&((cssLoader=function(paths,callback,params){var promises=[];angular.forEach(paths,(function(path){promises.push(buildElement("css",path,params))})),$q.all(promises).then((function(){callback()}),(function(err){callback(err)}))}).ocLazyLoadLoader=!0),angular.isUndefined(templatesLoader)&&((templatesLoader=function(paths,callback,params){var promises=[];return angular.forEach(paths,(function(url){var deferred=$q.defer();promises.push(deferred.promise),$http.get(url,params).success((function(data){angular.isString(data)&&data.length>0&&angular.forEach(angular.element(data),(function(node){"SCRIPT"===node.nodeName&&"text/ng-template"===node.type&&$templateCache.put(node.id,node.innerHTML)})),angular.isUndefined(filesCache.get(url))&&filesCache.put(url,!0),deferred.resolve()})).error((function(err){deferred.reject(new Error('Unable to load template file "'+url+'": '+err))}))})),$q.all(promises).then((function(){callback()}),(function(err){callback(err)}))}).ocLazyLoadLoader=!0);var filesLoader=function(config,params){var cssFiles=[],templatesFiles=[],jsFiles=[],promises=[],cachePromise=null;angular.extend(params||{},config);var pushFile=function(path){cachePromise=filesCache.get(path),angular.isUndefined(cachePromise)||!1===params.cache?/\.(css|less)[^\.]*$/.test(path)&&-1===cssFiles.indexOf(path)?cssFiles.push(path):/\.(htm|html)[^\.]*$/.test(path)&&-1===templatesFiles.indexOf(path)?templatesFiles.push(path):-1===jsFiles.indexOf(path)&&jsFiles.push(path):cachePromise&&promises.push(cachePromise)};if(params.serie?pushFile(params.files.shift()):angular.forEach(params.files,(function(path){pushFile(path)})),cssFiles.length>0){var cssDeferred=$q.defer();cssLoader(cssFiles,(function(err){angular.isDefined(err)&&cssLoader.hasOwnProperty("ocLazyLoadLoader")?($log.error(err),cssDeferred.reject(err)):cssDeferred.resolve()}),params),promises.push(cssDeferred.promise)}if(templatesFiles.length>0){var templatesDeferred=$q.defer();templatesLoader(templatesFiles,(function(err){angular.isDefined(err)&&templatesLoader.hasOwnProperty("ocLazyLoadLoader")?($log.error(err),templatesDeferred.reject(err)):templatesDeferred.resolve()}),params),promises.push(templatesDeferred.promise)}if(jsFiles.length>0){var jsDeferred=$q.defer();jsLoader(jsFiles,(function(err){angular.isDefined(err)&&jsLoader.hasOwnProperty("ocLazyLoadLoader")?($log.error(err),jsDeferred.reject(err)):jsDeferred.resolve()}),params),promises.push(jsDeferred.promise)}return params.serie&&params.files.length>0?$q.all(promises).then((function(){return filesLoader(config,params)})):$q.all(promises)};return{getModuleConfig:function(moduleName){if(!angular.isString(moduleName))throw new Error("You need to give the name of the module to get");return modules[moduleName]?modules[moduleName]:null},setModuleConfig:function(moduleConfig){if(!angular.isObject(moduleConfig))throw new Error("You need to give the module config object to set");return modules[moduleConfig.name]=moduleConfig,moduleConfig},getModules:function(){return regModules},isLoaded:function(modulesNames){var module,isLoaded;if(angular.isString(modulesNames)&&(modulesNames=[modulesNames]),angular.isArray(modulesNames)){var i,len;for(i=0,len=modulesNames.length;i<len;i++)if(module=modulesNames[i],isLoaded=void 0,(isLoaded=regModules.indexOf(module)>-1)||(isLoaded=!!moduleExists(module)),!isLoaded)return!1;return!0}throw new Error("You need to define the module(s) name(s)")},load:function(module,params){var moduleName,errText,self=this,config=null,moduleCache=[],deferredList=[],deferred=$q.defer();if(angular.isUndefined(params)&&(params={}),angular.isArray(module))return angular.forEach(module,(function(m){m&&deferredList.push(self.load(m,params))})),$q.all(deferredList).then((function(){deferred.resolve(module)}),(function(err){deferred.reject(err)})),deferred.promise;if(moduleName=getModuleName(module),"string"==typeof module?(config=self.getModuleConfig(module))||(config={files:[module]},moduleName=null):"object"==typeof module&&(config=self.setModuleConfig(module)),null===config?(errText='Module "'+moduleName+'" is not configured, cannot load.',$log.error(errText),deferred.reject(new Error(errText))):angular.isDefined(config.template)&&(angular.isUndefined(config.files)&&(config.files=[]),angular.isString(config.template)?config.files.push(config.template):angular.isArray(config.template)&&config.files.concat(config.template)),moduleCache.push=function(value){-1===this.indexOf(value)&&Array.prototype.push.apply(this,arguments)},angular.isDefined(moduleName)&&moduleExists(moduleName)&&-1!==regModules.indexOf(moduleName)&&(moduleCache.push(moduleName),angular.isUndefined(config.files)))return deferred.resolve(),deferred.promise;var localParams={};angular.extend(localParams,params,config);var loadDependencies=function loadDependencies(module){var moduleName,loadedModule,requires,diff,promisesList=[];if(null===(moduleName=getModuleName(module)))return $q.when();try{loadedModule=function(moduleName){try{return angular.module(moduleName)}catch(e){throw(/No module/.test(e)||e.message.indexOf("$injector:nomod")>-1)&&(e.message='The module "'+moduleName+'" that you are trying to load does not exist. '+e.message),e}}(moduleName)}catch(e){var deferred=$q.defer();return $log.error(e.message),deferred.reject(e),deferred.promise}return requires=function(module){var requires=[];return angular.forEach(module.requires,(function(requireModule){-1===regModules.indexOf(requireModule)&&requires.push(requireModule)})),requires}(loadedModule),angular.forEach(requires,(function(requireEntry){if("string"==typeof requireEntry){var config=self.getModuleConfig(requireEntry);if(null===config)return void moduleCache.push(requireEntry);requireEntry=config}moduleExists(requireEntry.name)?"string"!=typeof module&&(0!==(diff=requireEntry.files.filter((function(n){return self.getModuleConfig(requireEntry.name).files.indexOf(n)<0}))).length&&$log.warn('Module "',moduleName,'" attempted to redefine configuration for dependency. "',requireEntry.name,'"\n Additional Files Loaded:',diff),promisesList.push(filesLoader(requireEntry.files,localParams).then((function(){return loadDependencies(requireEntry)})))):("object"==typeof requireEntry&&(requireEntry.hasOwnProperty("name")&&requireEntry.name&&(self.setModuleConfig(requireEntry),moduleCache.push(requireEntry.name)),requireEntry.hasOwnProperty("css")&&0!==requireEntry.css.length&&angular.forEach(requireEntry.css,(function(path){buildElement("css",path,localParams)}))),requireEntry.hasOwnProperty("files")&&0!==requireEntry.files.length&&requireEntry.files&&promisesList.push(filesLoader(requireEntry,localParams).then((function(){return loadDependencies(requireEntry)}))))})),$q.all(promisesList)};return filesLoader(config,localParams).then((function(){null===moduleName?deferred.resolve(module):(moduleCache.push(moduleName),loadDependencies(moduleName).then((function(){try{justLoaded=[],function register(providers,registerModules,params){if(registerModules){var k,moduleName,moduleFn,tempRunBlocks=[];for(k=registerModules.length-1;k>=0;k--)if("string"!=typeof(moduleName=registerModules[k])&&(moduleName=getModuleName(moduleName)),moduleName&&-1===justLoaded.indexOf(moduleName)){var newModule=-1===regModules.indexOf(moduleName);if(moduleFn=angular.module(moduleName),newModule&&(regModules.push(moduleName),register(providers,moduleFn.requires,params)),moduleFn._runBlocks.length>0)for(runBlocks[moduleName]=[];moduleFn._runBlocks.length>0;)runBlocks[moduleName].push(moduleFn._runBlocks.shift());angular.isDefined(runBlocks[moduleName])&&(newModule||params.rerun)&&(tempRunBlocks=tempRunBlocks.concat(runBlocks[moduleName])),invokeQueue(providers,moduleFn._invokeQueue,moduleName,params.reconfig),invokeQueue(providers,moduleFn._configBlocks,moduleName,params.reconfig),broadcast(newModule?"ocLazyLoad.moduleLoaded":"ocLazyLoad.moduleReloaded",moduleName),registerModules.pop(),justLoaded.push(moduleName)}var instanceInjector=providers.getInstanceInjector();angular.forEach(tempRunBlocks,(function(fn){instanceInjector.invoke(fn)}))}}(providers,moduleCache,localParams)}catch(e){return $log.error(e.message),void deferred.reject(e)}deferred.resolve(module)}),(function(err){deferred.reject(err)})))}),(function(err){deferred.reject(err)})),deferred.promise}}}],this.config=function(config){if(angular.isDefined(config.jsLoader)||angular.isDefined(config.asyncLoader)){if(!angular.isFunction(config.jsLoader||config.asyncLoader))throw"The js loader needs to be a function";jsLoader=config.jsLoader||config.asyncLoader}if(angular.isDefined(config.cssLoader)){if(!angular.isFunction(config.cssLoader))throw"The css loader needs to be a function";cssLoader=config.cssLoader}if(angular.isDefined(config.templatesLoader)){if(!angular.isFunction(config.templatesLoader))throw"The template loader needs to be a function";templatesLoader=config.templatesLoader}angular.isDefined(config.modules)&&(angular.isArray(config.modules)?angular.forEach(config.modules,(function(moduleConfig){modules[moduleConfig.name]=moduleConfig})):modules[config.modules.name]=config.modules),angular.isDefined(config.debug)&&(debug=config.debug),angular.isDefined(config.events)&&(events=config.events)}}]),ocLazyLoad.directive("ocLazyLoad",["$ocLazyLoad","$compile","$animate","$parse",function($ocLazyLoad,$compile,$animate,$parse){return{restrict:"A",terminal:!0,priority:1e3,compile:function(element,attrs){var content=element[0].innerHTML;return element.html(""),function($scope,$element,$attr){var model=$parse($attr.ocLazyLoad);$scope.$watch((function(){return model($scope)||$attr.ocLazyLoad}),(function(moduleName){angular.isDefined(moduleName)&&$ocLazyLoad.load(moduleName).then((function(moduleConfig){$animate.enter($compile(content)($scope),null,$element)}))}),!0)}}}}]);var bootstrap=angular.bootstrap;angular.bootstrap=function(element,modules,config){return initModules=modules.slice(),bootstrap(element,modules,config)},Array.prototype.indexOf||(Array.prototype.indexOf=function(searchElement,fromIndex){var k;if(null==this)throw new TypeError('"this" is null or not defined');var O=Object(this),len=O.length>>>0;if(0===len)return-1;var n=+fromIndex||0;if(Math.abs(n)===1/0&&(n=0),n>=len)return-1;for(k=Math.max(n>=0?n:len-Math.abs(n),0);k<len;){if(k in O&&O[k]===searchElement)return k;k++}return-1})}();