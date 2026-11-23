package org.apache.spark.ui.storage;

import java.net.URLEncoder;
import org.apache.spark.status.api.v1.RDDPartitionInfo;
import org.apache.spark.ui.PagedTable;
import org.apache.spark.ui.PagedTable$class;
import org.apache.spark.util.Utils$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.xml.Elem;
import scala.xml.EntityRef;
import scala.xml.MetaData;
import scala.xml.Node;
import scala.xml.NodeBuffer;
import scala.xml.Null;
import scala.xml.Text;
import scala.xml.TopScope;
import scala.xml.Unparsed;
import scala.xml.UnprefixedAttribute;
import scala.xml.Unparsed.;

@ScalaSignature(
   bytes = "\u0006\u0001m4Q!\u0001\u0002\u0001\t1\u0011qB\u00117pG.\u0004\u0016mZ3e)\u0006\u0014G.\u001a\u0006\u0003\u0007\u0011\tqa\u001d;pe\u0006<WM\u0003\u0002\u0006\r\u0005\u0011Q/\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0019\u0001!D\n\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g!\r!RcF\u0007\u0002\t%\u0011a\u0003\u0002\u0002\u000b!\u0006<W\r\u001a+bE2,\u0007C\u0001\r\u001a\u001b\u0005\u0011\u0011B\u0001\u000e\u0003\u0005E\u0011En\\2l)\u0006\u0014G.\u001a*po\u0012\u000bG/\u0019\u0005\t9\u0001\u0011\t\u0011)A\u0005=\u0005A!-Y:f!\u0006$\bn\u0001\u0001\u0011\u0005}\u0011cB\u0001\b!\u0013\t\ts\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003G\u0011\u0012aa\u0015;sS:<'BA\u0011\u0010\u0011!1\u0003A!A!\u0002\u00139\u0013!\u0004:eIB\u000b'\u000f^5uS>t7\u000fE\u0002)aMr!!\u000b\u0018\u000f\u0005)jS\"A\u0016\u000b\u00051j\u0012A\u0002\u001fs_>$h(C\u0001\u0011\u0013\tys\"A\u0004qC\u000e\\\u0017mZ3\n\u0005E\u0012$aA*fc*\u0011qf\u0004\t\u0003imj\u0011!\u000e\u0006\u0003m]\n!A^\u0019\u000b\u0005aJ\u0014aA1qS*\u0011!HB\u0001\u0007gR\fG/^:\n\u0005q*$\u0001\u0005*E\tB\u000b'\u000f^5uS>t\u0017J\u001c4p\u0011!q\u0004A!A!\u0002\u0013y\u0014\u0001\u00039bO\u0016\u001c\u0016N_3\u0011\u00059\u0001\u0015BA!\u0010\u0005\rIe\u000e\u001e\u0005\t\u0007\u0002\u0011\t\u0011)A\u0005=\u0005Q1o\u001c:u\u0007>dW/\u001c8\t\u0011\u0015\u0003!\u0011!Q\u0001\n\u0019\u000bA\u0001Z3tGB\u0011abR\u0005\u0003\u0011>\u0011qAQ8pY\u0016\fg\u000eC\u0003K\u0001\u0011\u00051*\u0001\u0004=S:LGO\u0010\u000b\u0007\u00196su\nU)\u0011\u0005a\u0001\u0001\"\u0002\u000fJ\u0001\u0004q\u0002\"\u0002\u0014J\u0001\u00049\u0003\"\u0002 J\u0001\u0004y\u0004\"B\"J\u0001\u0004q\u0002\"B#J\u0001\u00041\u0005\"B*\u0001\t\u0003\"\u0016a\u0002;bE2,\u0017\nZ\u000b\u0002=!)a\u000b\u0001C!)\u0006iA/\u00192mK\u000e\u001b8o\u00117bgNDQ\u0001\u0017\u0001\u0005BQ\u000b\u0011\u0003]1hKNK'0\u001a$pe64\u0015.\u001a7e\u0011\u0015Q\u0006\u0001\"\u0011U\u0003U\u0001(/\u001a<QC\u001e,7+\u001b>f\r>\u0014XNR5fY\u0012DQ\u0001\u0018\u0001\u0005BQ\u000b1\u0003]1hK:+XNY3s\r>\u0014XNR5fY\u0012DqA\u0018\u0001C\u0002\u0013\u0005s,\u0001\u0006eCR\f7k\\;sG\u0016,\u0012\u0001\u0019\t\u00031\u0005L!A\u0019\u0002\u0003\u001f\tcwnY6ECR\f7k\\;sG\u0016Da\u0001\u001a\u0001!\u0002\u0013\u0001\u0017a\u00033bi\u0006\u001cv.\u001e:dK\u0002BQA\u001a\u0001\u0005B\u001d\f\u0001\u0002]1hK2Kgn\u001b\u000b\u0003=!DQ![3A\u0002}\nA\u0001]1hK\")1\u000e\u0001C!)\u0006\u0001rm\u001c\"viR|gNR8s[B\u000bG\u000f\u001b\u0005\u0006[\u0002!\tE\\\u0001\bQ\u0016\fG-\u001a:t+\u0005y\u0007c\u0001\u00151aB\u0011\u0011\u000f^\u0007\u0002e*\u00111oD\u0001\u0004q6d\u0017BA;s\u0005\u0011qu\u000eZ3\t\u000b]\u0004A\u0011\t=\u0002\u0007I|w\u000f\u0006\u0002ps\")!P\u001ea\u0001/\u0005)!\r\\8dW\u0002"
)
public class BlockPagedTable implements PagedTable<BlockTableRowData> {
   public final String org$apache$spark$ui$storage$BlockPagedTable$$basePath;
   public final int org$apache$spark$ui$storage$BlockPagedTable$$pageSize;
   public final String org$apache$spark$ui$storage$BlockPagedTable$$sortColumn;
   public final boolean org$apache$spark$ui$storage$BlockPagedTable$$desc;
   private final BlockDataSource dataSource;

   public Seq<Node> table(int page) {
      return PagedTable$class.table(this, page);
   }

   public Seq<Node> pageNavigation(int page, int pageSize, int totalPages) {
      return PagedTable$class.pageNavigation(this, page, pageSize, totalPages);
   }

   public String tableId() {
      return "rdd-storage-by-block-table";
   }

   public String tableCssClass() {
      return "table table-bordered table-condensed table-striped table-head-clickable";
   }

   public String pageSizeFormField() {
      return "block.pageSize";
   }

   public String prevPageSizeFormField() {
      return "block.prevPageSize";
   }

   public String pageNumberFormField() {
      return "block.page";
   }

   public BlockDataSource dataSource() {
      return this.dataSource;
   }

   public String pageLink(int page) {
      String encodedSortColumn = URLEncoder.encode(this.org$apache$spark$ui$storage$BlockPagedTable$$sortColumn, "UTF-8");
      return (new StringBuilder()).append((Object)this.org$apache$spark$ui$storage$BlockPagedTable$$basePath).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", "=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.pageNumberFormField(), BoxesRunTime.boxToInteger(page)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&block.sort=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{encodedSortColumn}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&block.desc=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToBoolean(this.org$apache$spark$ui$storage$BlockPagedTable$$desc)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&", "=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.pageSizeFormField(), BoxesRunTime.boxToInteger(this.org$apache$spark$ui$storage$BlockPagedTable$$pageSize)}))).toString();
   }

   public String goButtonFormPath() {
      String encodedSortColumn = URLEncoder.encode(this.org$apache$spark$ui$storage$BlockPagedTable$$sortColumn, "UTF-8");
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "&block.sort=", "&block.desc=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$ui$storage$BlockPagedTable$$basePath, encodedSortColumn, BoxesRunTime.boxToBoolean(this.org$apache$spark$ui$storage$BlockPagedTable$$desc)}));
   }

   public Seq<Node> headers() {
      Seq blockHeaders = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block Name", "Storage Level", "Size in Memory", "Size on Disk", "Executors"})));
      if (blockHeaders.contains(this.org$apache$spark$ui$storage$BlockPagedTable$$sortColumn)) {
         Seq headerRow = (Seq)blockHeaders.map(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final BlockPagedTable $outer;

            public final Elem apply(String header) {
               Elem var10000;
               label38: {
                  String var2 = this.$outer.org$apache$spark$ui$storage$BlockPagedTable$$sortColumn;
                  if (header == null) {
                     if (var2 != null) {
                        break label38;
                     }
                  } else if (!header.equals(var2)) {
                     break label38;
                  }

                  Unparsed headerLink = .MODULE$.apply((new StringBuilder()).append((Object)this.$outer.org$apache$spark$ui$storage$BlockPagedTable$$basePath).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&block.sort=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{URLEncoder.encode(header, "UTF-8")}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&block.desc=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToBoolean(!this.$outer.org$apache$spark$ui$storage$BlockPagedTable$$desc)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&block.pageSize=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ui$storage$BlockPagedTable$$pageSize)}))).toString());
                  String arrow = this.$outer.org$apache$spark$ui$storage$BlockPagedTable$$desc ? "&#x25BE;" : "&#x25B4;";
                  Null var10004 = scala.xml.Null..MODULE$;
                  TopScope var10005 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("\n            "));
                  MetaData $md = scala.xml.Null..MODULE$;
                  MetaData var13 = new UnprefixedAttribute("href", headerLink, $md);
                  TopScope var10013 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new Text("\n              "));
                  $buf.$amp$plus(header);
                  $buf.$amp$plus(new Text("\n              "));
                  Null var10020 = scala.xml.Null..MODULE$;
                  TopScope var10021 = scala.xml.TopScope..MODULE$;
                  NodeBuffer $buf = new NodeBuffer();
                  $buf.$amp$plus(new EntityRef("nbsp"));
                  $buf.$amp$plus(.MODULE$.apply(arrow));
                  $buf.$amp$plus(new Elem((String)null, "span", var10020, var10021, false, $buf));
                  $buf.$amp$plus(new Text("\n            "));
                  $buf.$amp$plus(new Elem((String)null, "a", var13, var10013, false, $buf));
                  $buf.$amp$plus(new Text("\n          "));
                  var10000 = new Elem((String)null, "th", var10004, var10005, false, $buf);
                  return var10000;
               }

               Unparsed headerLink = .MODULE$.apply((new StringBuilder()).append((Object)this.$outer.org$apache$spark$ui$storage$BlockPagedTable$$basePath).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&block.sort=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{URLEncoder.encode(header, "UTF-8")}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"&block.pageSize=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$ui$storage$BlockPagedTable$$pageSize)}))).toString());
               Null var15 = scala.xml.Null..MODULE$;
               TopScope var16 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text("\n            "));
               MetaData $md = scala.xml.Null..MODULE$;
               MetaData var14 = new UnprefixedAttribute("href", headerLink, $md);
               TopScope var17 = scala.xml.TopScope..MODULE$;
               NodeBuffer $buf = new NodeBuffer();
               $buf.$amp$plus(new Text("\n              "));
               $buf.$amp$plus(header);
               $buf.$amp$plus(new Text("\n            "));
               $buf.$amp$plus(new Elem((String)null, "a", var14, var17, false, $buf));
               $buf.$amp$plus(new Text("\n          "));
               var10000 = new Elem((String)null, "th", var15, var16, false, $buf);
               return var10000;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, Seq$.MODULE$.canBuildFrom());
         Null var10004 = scala.xml.Null..MODULE$;
         TopScope var10005 = scala.xml.TopScope..MODULE$;
         NodeBuffer $buf = new NodeBuffer();
         $buf.$amp$plus(headerRow);
         return new Elem((String)null, "thead", var10004, var10005, false, $buf);
      } else {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unknown column: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$ui$storage$BlockPagedTable$$sortColumn})));
      }
   }

   public Seq<Node> row(BlockTableRowData block) {
      Null var10004 = scala.xml.Null..MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      "));
      Null var10012 = scala.xml.Null..MODULE$;
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(block.blockName());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(block.storageLevel());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(Utils$.MODULE$.bytesToString(block.memoryUsed()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(Utils$.MODULE$.bytesToString(block.diskUsed()));
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      "));
      var10012 = scala.xml.Null..MODULE$;
      var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(block.executors());
      $buf.$amp$plus(new Elem((String)null, "td", var10012, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n    "));
      return new Elem((String)null, "tr", var10004, var10005, false, $buf);
   }

   public BlockPagedTable(String basePath, Seq<RDDPartitionInfo> rddPartitions, int pageSize, String sortColumn, boolean desc) {
      this.org$apache$spark$ui$storage$BlockPagedTable$$basePath = basePath;
      this.org$apache$spark$ui$storage$BlockPagedTable$$pageSize = pageSize;
      this.org$apache$spark$ui$storage$BlockPagedTable$$sortColumn = sortColumn;
      this.org$apache$spark$ui$storage$BlockPagedTable$$desc = desc;
      PagedTable$class.$init$(this);
      this.dataSource = new BlockDataSource(rddPartitions, pageSize, sortColumn, desc);
   }
}
