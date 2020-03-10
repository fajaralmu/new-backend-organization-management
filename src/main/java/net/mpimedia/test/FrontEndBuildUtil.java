package net.mpimedia.test;

public class FrontEndBuildUtil {
	
	static final String CODE = "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"/><link rel=\"icon\" href=\"/favicon.ico\"/><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"/><meta name=\"theme-color\" content=\"#000000\"/><meta name=\"description\" content=\"Web site created using create-react-app\"/><link rel=\"apple-touch-icon\" href=\"/logo192.png\"/><link rel=\"manifest\" href=\"/manifest.json\"/><title>Mahasiswa Pencinta Islam</title><link href=\"/static/css/main.487fb9ff.chunk.css\" rel=\"stylesheet\"></head><body><noscript>You need to enable JavaScript to run this app.</noscript><input type=\"hidden\" id=\"rootPath\" value=\"${basePath}\"/><div id=\"root\"></div><script>!function(l){function e(e){for(var t,r,n=e[0],o=e[1],u=e[2],a=0,i=[];a<n.length;a++)r=n[a],Object.prototype.hasOwnProperty.call(p,r)&&p[r]&&i.push(p[r][0]),p[r]=0;for(t in o)Object.prototype.hasOwnProperty.call(o,t)&&(l[t]=o[t]);for(s&&s(e);i.length;)i.shift()();return c.push.apply(c,u||[]),f()}function f(){for(var e,t=0;t<c.length;t++){for(var r=c[t],n=!0,o=1;o<r.length;o++){var u=r[o];0!==p[u]&&(n=!1)}n&&(c.splice(t--,1),e=a(a.s=r[0]))}return e}var r={},p={1:0},c=[];function a(e){if(r[e])return r[e].exports;var t=r[e]={i:e,l:!1,exports:{}};return l[e].call(t.exports,t,t.exports,a),t.l=!0,t.exports}a.m=l,a.c=r,a.d=function(e,t,r){a.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:r})},a.r=function(e){\"undefined\"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:\"Module\"}),Object.defineProperty(e,\"__esModule\",{value:!0})},a.t=function(t,e){if(1&e&&(t=a(t)),8&e)return t;if(4&e&&\"object\"==typeof t&&t&&t.__esModule)return t;var r=Object.create(null);if(a.r(r),Object.defineProperty(r,\"default\",{enumerable:!0,value:t}),2&e&&\"string\"!=typeof t)for(var n in t)a.d(r,n,function(e){return t[e]}.bind(null,n));return r},a.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return a.d(t,\"a\",t),t},a.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},a.p=\"/\";var t=this[\"webpackJsonporganization-management\"]=this[\"webpackJsonporganization-management\"]||[],n=t.push.bind(t);t.push=e,t=t.slice();for(var o=0;o<t.length;o++)e(t[o]);var s=n;f()}([])</script><script src=\"/static/js/2.62ca9f6a.chunk.js\"></script><script src=\"/static/js/main.ee642736.chunk.js\"></script></body></html>";
	static final String BASE_URL = "${baseResourcePath }";
	
	public static void main(String[] args) {
		replace();
	}
	
	static void replace() {
		
		String srcReplaced = CODE.replace("src=\"", "src=\""+BASE_URL);
		String hrefReplaced = srcReplaced.replace("href=\"", "href=\""+BASE_URL);
		
		System.out.println(hrefReplaced);
		
	}
}
