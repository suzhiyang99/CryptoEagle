package org.apache.spark.ui;

import java.net.URLDecoder;
import org.apache.spark.util.Utils$;
import org.spark_project.guava.base.Splitter;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterable;
import scala.collection.Iterable$;
import scala.collection.JavaConverters$;
import scala.collection.MapLike;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.IndexedSeq$;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;
import scala.xml.Elem;
import scala.xml.EntityRef;
import scala.xml.MetaData;
import scala.xml.Node;
import scala.xml.NodeBuffer;
import scala.xml.Null;
import scala.xml.Text;
import scala.xml.TopScope;
import scala.xml.UnprefixedAttribute;
import scala.xml.Null.;

public abstract class PagedTable$class {
   public static Seq table(PagedTable $this, int page) {
      PagedDataSource _dataSource = $this.dataSource();

      Elem var10000;
      try {
         PageData var14 = _dataSource.pageData(page);
         if (var14 == null) {
            throw new MatchError(var14);
         }

         int totalPages = var14.totalPage();
         Seq data = var14.data();
         Tuple2 var17 = new Tuple2(BoxesRunTime.boxToInteger(totalPages), data);
         int totalPages = var17._1$mcI$sp();
         Seq data = (Seq)var17._2();
         Seq pageNavi = $this.pageNavigation(page, _dataSource.pageSize(), totalPages);
         Null var29 = .MODULE$;
         TopScope var30 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(pageNavi);
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var27 = new UnprefixedAttribute("id", $this.tableId(), $md);
         var27 = new UnprefixedAttribute("class", $this.tableCssClass(), var27);
         TopScope var31 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus($this.headers());
         $buf.$amp$plus(new Text("\n          "));
         Null var33 = .MODULE$;
         TopScope var35 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(data.map(new Serializable($this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final PagedTable $outer;

            public final Seq<Node> apply(T t) {
               return this.$outer.row(t);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, Seq$.MODULE$.canBuildFrom()));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "tbody", var33, var35, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(new Elem((String)null, "table", var27, var31, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(pageNavi);
         $buf.$amp$plus(new Text("\n      "));
         var10000 = new Elem((String)null, "div", var29, var30, false, $buf);
      } catch (IndexOutOfBoundsException var25) {
         PageData var5 = _dataSource.pageData(1);
         if (var5 == null) {
            throw new MatchError(var5);
         }

         int totalPages = var5.totalPage();
         Null var10004 = .MODULE$;
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus($this.pageNavigation(1, _dataSource.pageSize(), totalPages));
         $buf.$amp$plus(new Text("\n          "));
         MetaData $md = .MODULE$;
         MetaData var26 = new UnprefixedAttribute("class", new Text("alert alert-error"), $md);
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         Null var10020 = .MODULE$;
         TopScope var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Error while rendering table:"));
         $buf.$amp$plus(new Elem((String)null, "p", var10020, var10021, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         var10020 = .MODULE$;
         var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n              "));
         $buf.$amp$plus(Utils$.MODULE$.exceptionString(var25));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(new Elem((String)null, "pre", var10020, var10021, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "div", var26, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         var10000 = new Elem((String)null, "div", var10004, var10005, false, $buf);
      }

      return var10000;
   }

   public static Seq pageNavigation(PagedTable $this, int page, int pageSize, int totalPages) {
      Object var10000;
      if (totalPages == 1) {
         var10000 = Nil$.MODULE$;
      } else {
         int groupSize = 10;
         int firstGroup = 0;
         int lastGroup = (totalPages - 1) / groupSize;
         int currentGroup = (page - 1) / groupSize;
         int startPage = currentGroup * groupSize + 1;
         int endPage = RichInt$.MODULE$.min$extension(Predef$.MODULE$.intWrapper(totalPages), startPage + groupSize - 1);
         IndexedSeq pageTags = (IndexedSeq)RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(startPage), endPage).map(new Serializable($this, page) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final PagedTable $outer;
            private final int page$1;

            public final Elem apply(int p) {
               Elem var10000;
               if (p == this.page$1) {
                  MetaData $md = .MODULE$;
                  MetaData var9 = new UnprefixedAttribute("class", new Text("disabled"), $md);
                  TopScope var10005 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  MetaData $md = .MODULE$;
                  MetaData var10 = new UnprefixedAttribute("href", new Text("#"), $md);
                  TopScope var10013 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(BoxesRunTime.boxToInteger(p));
                  $buf.$amp$plus(new Elem((String)null, "a", var10, var10013, false, $buf));
                  var10000 = new Elem((String)null, "li", var9, var10005, false, $buf);
               } else {
                  Null var10004 = .MODULE$;
                  TopScope var12 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  MetaData $md = .MODULE$;
                  MetaData var11 = new UnprefixedAttribute("href", scala.xml.Unparsed..MODULE$.apply(this.$outer.pageLink(p)), $md);
                  TopScope var13 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(BoxesRunTime.boxToInteger(p));
                  $buf.$amp$plus(new Elem((String)null, "a", var11, var13, false, $buf));
                  var10000 = new Elem((String)null, "li", var10004, var12, false, $buf);
               }

               return var10000;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.page$1 = page$1;
               }
            }
         }, IndexedSeq$.MODULE$.canBuildFrom());
         Iterable var86;
         if ((new StringOps(Predef$.MODULE$.augmentString($this.goButtonFormPath()))).contains(BoxesRunTime.boxToCharacter('?'))) {
            String queryString = $this.goButtonFormPath().split("\\?", 2)[1];
            String search = queryString.split("#")[0];
            var86 = (Iterable)((MapLike)JavaConverters$.MODULE$.mapAsScalaMapConverter(Splitter.on('&').trimResults().omitEmptyStrings().withKeyValueSeparator("=").split(search)).asScala()).filterKeys(new Serializable($this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final PagedTable $outer;

               public final boolean apply(String x$2) {
                  boolean var10000;
                  label23: {
                     String var2 = this.$outer.pageSizeFormField();
                     if (x$2 == null) {
                        if (var2 == null) {
                           break label23;
                        }
                     } else if (x$2.equals(var2)) {
                        break label23;
                     }

                     var10000 = true;
                     return var10000;
                  }

                  var10000 = false;
                  return var10000;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }).filterKeys(new Serializable($this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final PagedTable $outer;

               public final boolean apply(String x$3) {
                  boolean var10000;
                  label23: {
                     String var2 = this.$outer.prevPageSizeFormField();
                     if (x$3 == null) {
                        if (var2 == null) {
                           break label23;
                        }
                     } else if (x$3.equals(var2)) {
                        break label23;
                     }

                     var10000 = true;
                     return var10000;
                  }

                  var10000 = false;
                  return var10000;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }).filterKeys(new Serializable($this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final PagedTable $outer;

               public final boolean apply(String x$4) {
                  boolean var10000;
                  label23: {
                     String var2 = this.$outer.pageNumberFormField();
                     if (x$4 == null) {
                        if (var2 == null) {
                           break label23;
                        }
                     } else if (x$4.equals(var2)) {
                        break label23;
                     }

                     var10000 = true;
                     return var10000;
                  }

                  var10000 = false;
                  return var10000;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }).mapValues(new Serializable($this) {
               public static final long serialVersionUID = 0L;

               public final String apply(String x$5) {
                  return URLDecoder.decode(x$5, "UTF-8");
               }
            }).map(new Serializable($this) {
               public static final long serialVersionUID = 0L;

               public final Elem apply(Tuple2<String, String> x0$1) {
                  if (x0$1 != null) {
                     String k = (String)x0$1._1();
                     String v = (String)x0$1._2();
                     MetaData $md = .MODULE$;
                     MetaData var7 = new UnprefixedAttribute("value", v, $md);
                     var7 = new UnprefixedAttribute("name", k, var7);
                     var7 = new UnprefixedAttribute("type", new Text("hidden"), var7);
                     Elem var5 = new Elem((String)null, "input", var7, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0])));
                     return var5;
                  } else {
                     throw new MatchError(x0$1);
                  }
               }
            }, Iterable$.MODULE$.canBuildFrom());
         } else {
            var86 = (Iterable)Seq$.MODULE$.empty();
         }

         Iterable hiddenFormFields = var86;
         var10000 = new Elem;
         Null var10004 = .MODULE$;
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n        "));
         Null var10012 = .MODULE$;
         TopScope var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         MetaData $md = .MODULE$;
         MetaData var51 = new UnprefixedAttribute("style", new Text("margin-bottom: 0px;"), $md);
         var51 = new UnprefixedAttribute("class", new Text("form-inline pull-right"), var51);
         var51 = new UnprefixedAttribute("action", scala.xml.Unparsed..MODULE$.apply($this.goButtonFormPath()), var51);
         var51 = new UnprefixedAttribute("method", new Text("get"), var51);
         var51 = new UnprefixedAttribute("id", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"form-", "-page"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{$this.tableId()})), var51);
         TopScope var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = .MODULE$;
         MetaData var56 = new UnprefixedAttribute("value", BoxesRunTime.boxToInteger(pageSize).toString(), $md);
         var56 = new UnprefixedAttribute("name", $this.prevPageSizeFormField(), var56);
         var56 = new UnprefixedAttribute("type", new Text("hidden"), var56);
         $buf.$amp$plus(new Elem((String)null, "input", var56, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(hiddenFormFields);
         $buf.$amp$plus(new Text("\n            "));
         Null var10028 = .MODULE$;
         TopScope var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(BoxesRunTime.boxToInteger(totalPages));
         $buf.$amp$plus(new Text(" Pages. Jump to"));
         $buf.$amp$plus(new Elem((String)null, "label", var10028, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = .MODULE$;
         MetaData var59 = new UnprefixedAttribute("class", new Text("span1"), $md);
         var59 = new UnprefixedAttribute("value", BoxesRunTime.boxToInteger(page).toString(), var59);
         var59 = new UnprefixedAttribute("id", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"form-", "-page-no"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{$this.tableId()})), var59);
         var59 = new UnprefixedAttribute("name", $this.pageNumberFormField(), var59);
         var59 = new UnprefixedAttribute("type", new Text("text"), var59);
         $buf.$amp$plus(new Elem((String)null, "input", var59, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n\n            "));
         var10028 = .MODULE$;
         var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text(". Show "));
         $buf.$amp$plus(new Elem((String)null, "label", var10028, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n            "));
         MetaData $md = .MODULE$;
         MetaData var64 = new UnprefixedAttribute("class", new Text("span1"), $md);
         var64 = new UnprefixedAttribute("value", BoxesRunTime.boxToInteger(pageSize).toString(), var64);
         var64 = new UnprefixedAttribute("name", $this.pageSizeFormField(), var64);
         var64 = new UnprefixedAttribute("id", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"form-", "-page-size"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{$this.tableId()})), var64);
         var64 = new UnprefixedAttribute("type", new Text("text"), var64);
         $buf.$amp$plus(new Elem((String)null, "input", var64, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
         $buf.$amp$plus(new Text("\n            "));
         var10028 = .MODULE$;
         var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("items in a page."));
         $buf.$amp$plus(new Elem((String)null, "label", var10028, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n\n            "));
         MetaData $md = .MODULE$;
         MetaData var69 = new UnprefixedAttribute("class", new Text("btn"), $md);
         var69 = new UnprefixedAttribute("type", new Text("submit"), var69);
         var10029 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Go"));
         $buf.$amp$plus(new Elem((String)null, "button", var69, var10029, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         $buf.$amp$plus(new Elem((String)null, "form", var51, var10021, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         $buf.$amp$plus(new Elem((String)null, "div", var10012, var10013, false, $buf));
         $buf.$amp$plus(new Text("\n        "));
         MetaData $md = .MODULE$;
         MetaData var71 = new UnprefixedAttribute("style", new Text("margin-bottom: 0px;"), $md);
         var71 = new UnprefixedAttribute("class", new Text("pagination"), var71);
         Elem var10008 = new Elem;
         var10013 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n          "));
         MetaData $md = .MODULE$;
         MetaData var73 = new UnprefixedAttribute("style", new Text("float: left; padding-top: 4px; padding-right: 4px;"), $md);
         var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("Page: "));
         $buf.$amp$plus(new Elem((String)null, "span", var73, var10021, false, $buf));
         $buf.$amp$plus(new Text("\n          "));
         Elem var10016 = new Elem;
         Null var10020 = .MODULE$;
         var10021 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(new Text("\n            "));
         Object var10024;
         if (currentGroup > firstGroup) {
            var10028 = .MODULE$;
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              "));
            MetaData $md = .MODULE$;
            MetaData var74 = new UnprefixedAttribute("aria-label", new Text("Previous Group"), $md);
            var74 = new UnprefixedAttribute("href", scala.xml.Unparsed..MODULE$.apply($this.pageLink(startPage - groupSize)), var74);
            TopScope var10037 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                "));
            MetaData $md = .MODULE$;
            MetaData var76 = new UnprefixedAttribute("aria-hidden", new Text("true"), $md);
            TopScope var10045 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                  "));
            $buf.$amp$plus(new EntityRef("lt"));
            $buf.$amp$plus(new EntityRef("lt"));
            $buf.$amp$plus(new Text("\n                "));
            $buf.$amp$plus(new Elem((String)null, "span", var76, var10045, false, $buf));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus(new Elem((String)null, "a", var74, var10037, false, $buf));
            $buf.$amp$plus(new Text("\n            "));
            var10024 = new Elem((String)null, "li", var10028, var10029, false, $buf);
         } else {
            var10024 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10024);
         $buf.$amp$plus(new Text("\n            "));
         if (page > 1) {
            var10028 = .MODULE$;
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n            "));
            MetaData $md = .MODULE$;
            MetaData var77 = new UnprefixedAttribute("aria-label", new Text("Previous"), $md);
            var77 = new UnprefixedAttribute("href", scala.xml.Unparsed..MODULE$.apply($this.pageLink(page - 1)), var77);
            TopScope var106 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              "));
            MetaData $md = .MODULE$;
            MetaData var79 = new UnprefixedAttribute("aria-hidden", new Text("true"), $md);
            TopScope var109 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                "));
            $buf.$amp$plus(new EntityRef("lt"));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus(new Elem((String)null, "span", var79, var109, false, $buf));
            $buf.$amp$plus(new Text("\n            "));
            $buf.$amp$plus(new Elem((String)null, "a", var77, var106, false, $buf));
            $buf.$amp$plus(new Text("\n            "));
            var10024 = new Elem((String)null, "li", var10028, var10029, false, $buf);
         } else {
            var10024 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10024);
         $buf.$amp$plus(new Text("\n            "));
         $buf.$amp$plus(pageTags);
         $buf.$amp$plus(new Text("\n            "));
         if (page < totalPages) {
            var10028 = .MODULE$;
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              "));
            MetaData $md = .MODULE$;
            MetaData var80 = new UnprefixedAttribute("aria-label", new Text("Next"), $md);
            var80 = new UnprefixedAttribute("href", scala.xml.Unparsed..MODULE$.apply($this.pageLink(page + 1)), var80);
            TopScope var107 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                "));
            MetaData $md = .MODULE$;
            MetaData var82 = new UnprefixedAttribute("aria-hidden", new Text("true"), $md);
            TopScope var110 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new EntityRef("gt"));
            $buf.$amp$plus(new Elem((String)null, "span", var82, var110, false, $buf));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus(new Elem((String)null, "a", var80, var107, false, $buf));
            $buf.$amp$plus(new Text("\n            "));
            var10024 = new Elem((String)null, "li", var10028, var10029, false, $buf);
         } else {
            var10024 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10024);
         $buf.$amp$plus(new Text("\n            "));
         if (currentGroup < lastGroup) {
            var10028 = .MODULE$;
            var10029 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n              "));
            MetaData $md = .MODULE$;
            MetaData var83 = new UnprefixedAttribute("aria-label", new Text("Next Group"), $md);
            var83 = new UnprefixedAttribute("href", scala.xml.Unparsed..MODULE$.apply($this.pageLink(startPage + groupSize)), var83);
            TopScope var108 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                "));
            MetaData $md = .MODULE$;
            MetaData var85 = new UnprefixedAttribute("aria-hidden", new Text("true"), $md);
            TopScope var111 = scala.xml.TopScope..MODULE$;
            NodeBuffer $buf = new NodeBuffer();
            $buf.$amp$plus(new Text("\n                  "));
            $buf.$amp$plus(new EntityRef("gt"));
            $buf.$amp$plus(new EntityRef("gt"));
            $buf.$amp$plus(new Text("\n                "));
            $buf.$amp$plus(new Elem((String)null, "span", var85, var111, false, $buf));
            $buf.$amp$plus(new Text("\n              "));
            $buf.$amp$plus(new Elem((String)null, "a", var83, var108, false, $buf));
            $buf.$amp$plus(new Text("\n            "));
            var10024 = new Elem((String)null, "li", var10028, var10029, false, $buf);
         } else {
            var10024 = BoxedUnit.UNIT;
         }

         $buf.$amp$plus(var10024);
         $buf.$amp$plus(new Text("\n          "));
         var10016.<init>((String)null, "ul", var10020, var10021, false, $buf);
         $buf.$amp$plus(var10016);
         $buf.$amp$plus(new Text("\n        "));
         var10008.<init>((String)null, "div", var71, var10013, false, $buf);
         $buf.$amp$plus(var10008);
         $buf.$amp$plus(new Text("\n      "));
         var10000.<init>((String)null, "div", var10004, var10005, false, $buf);
      }

      return (Seq)var10000;
   }

   public static void $init$(PagedTable $this) {
   }
}
