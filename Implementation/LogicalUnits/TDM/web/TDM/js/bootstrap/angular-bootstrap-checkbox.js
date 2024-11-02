"use strict";angular.module("ui.checkbox",[]).directive("checkbox",(function(){return{scope:{},require:"ngModel",restrict:"E",replace:"true",template:"<button type=\"button\" ng-style=\"stylebtn\" class=\"btn btn-white\" ng-class=\"{'btn-xs': size==='default', 'btn-sm': size==='large', 'btn-lg': size==='largest'}\"><span ng-style=\"styleicon\" class=\"glyphicon\" ng-class=\"{'fa fa-check': checked===true}\"></span></button>",link:function(scope,elem,attrs,modelCtrl){var label=elem.next("span");scope.size="default",scope.stylebtn={},scope.styleicon={width:"10px",left:"-1px"},void 0!==attrs.large&&(scope.size="large",scope.stylebtn={"padding-top":"2px","padding-bottom":"2px",height:"30px"},scope.styleicon={width:"8px",left:"-5px","font-size":"17px"}),void 0!==attrs.larger&&(scope.size="larger",scope.stylebtn={"padding-top":"2px","padding-bottom":"2px",height:"34px"},scope.styleicon={width:"8px",left:"-8px","font-size":"22px"}),void 0!==attrs.largest&&(scope.size="largest",scope.stylebtn={"padding-top":"2px","padding-bottom":"2px",height:"45px"},scope.styleicon={width:"11px",left:"-11px","font-size":"30px"});var trueValue=!0,falseValue=!1;void 0!==attrs.ngTrueValue&&(trueValue=attrs.ngTrueValue),void 0!==attrs.ngFalseValue&&(falseValue=attrs.ngFalseValue),void 0!==scope.name&&(elem.name=scope.name),scope.$watch((function(){return modelCtrl.$modelValue===trueValue||!0===modelCtrl.$modelValue?(modelCtrl.$setViewValue(trueValue),label.addClass("todo-completed")):modelCtrl.$setViewValue(falseValue),modelCtrl.$modelValue}),(function(newVal,oldVal){scope.checked=modelCtrl.$modelValue===trueValue}),!0),elem.bind("click",(function(){scope.$apply((function(){modelCtrl.$modelValue===falseValue?(modelCtrl.$setViewValue(trueValue),label.toggleClass("todo-completed")):(modelCtrl.$setViewValue(falseValue),label.toggleClass("todo-completed"))}))}))}}}));