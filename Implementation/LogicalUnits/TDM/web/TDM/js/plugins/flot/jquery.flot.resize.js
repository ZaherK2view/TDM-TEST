!function($,e,t){var a,i=[],n=$.resize=$.extend($.resize,{}),r=!1,s="setTimeout",u="resize",m=u+"-special-event",o="pendingDelay",l="activeDelay",f="throttleWindow";function h(t){!0===r&&(r=t||1);for(var s=i.length-1;s>=0;s--){var l=$(i[s]);if(l[0]==e||l.is(":visible")){var f=l.width(),c=l.height(),d=l.data(m);!d||f===d.w&&c===d.h||(l.trigger(u,[d.w=f,d.h=c]),r=t||!0)}else(d=l.data(m)).w=0,d.h=0}null!==a&&(r&&(null==t||t-r<1e3)?a=e.requestAnimationFrame(h):(a=setTimeout(h,n[o]),r=!1))}n[o]=200,n[l]=20,n[f]=!0,$.event.special[u]={setup:function(){if(!n[f]&&this[s])return!1;var e=$(this);i.push(this),e.data(m,{w:e.width(),h:e.height()}),1===i.length&&(a=t,h())},teardown:function(){if(!n[f]&&this[s])return!1;for(var e=$(this),t=i.length-1;t>=0;t--)if(i[t]==this){i.splice(t,1);break}e.removeData(m),i.length||(r?cancelAnimationFrame(a):clearTimeout(a),a=null)},add:function(e){if(!n[f]&&this[s])return!1;var i;function a(e,n,a){var r=$(this),s=r.data(m)||{};s.w=n!==t?n:r.width(),s.h=a!==t?a:r.height(),i.apply(this,arguments)}if($.isFunction(e))return i=e,a;i=e.handler,e.handler=a}},e.requestAnimationFrame||(e.requestAnimationFrame=e.webkitRequestAnimationFrame||e.mozRequestAnimationFrame||e.oRequestAnimationFrame||e.msRequestAnimationFrame||function(t,i){return e.setTimeout((function(){t((new Date).getTime())}),n[l])}),e.cancelAnimationFrame||(e.cancelAnimationFrame=e.webkitCancelRequestAnimationFrame||e.mozCancelRequestAnimationFrame||e.oCancelRequestAnimationFrame||e.msCancelRequestAnimationFrame||clearTimeout)}(jQuery,this),jQuery.plot.plugins.push({init:function(plot){function onResize(){var placeholder=plot.getPlaceholder();0!=placeholder.width()&&0!=placeholder.height()&&(plot.resize(),plot.setupGrid(),plot.draw())}plot.hooks.bindEvents.push((function(plot,eventHolder){plot.getPlaceholder().resize(onResize)})),plot.hooks.shutdown.push((function(plot,eventHolder){plot.getPlaceholder().unbind("resize",onResize)}))},options:{},name:"resize",version:"1.0"});