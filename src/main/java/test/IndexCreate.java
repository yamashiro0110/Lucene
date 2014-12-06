package test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

/**
 * インデックス生成のテスト
 * @author yamashiro-r
 */
public class IndexCreate {

    public static void main(String[] args) {
        IndexCreate indexCreate = new IndexCreate();
        indexCreate.createIndex();
    }

    /**
     * インデックス配置先
     */
    private static final String INDEX_DIR = "index";

    /**
     * インデックスの件数
     */
    private static final int INDEX_COUNT = 10000;

    /**
     * インデックスを生成する
     */
    public void createIndex() {
        System.out.println("インデックス生成を開始します");

        // アナライザ
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);

        // インデックス生成の設定
        IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_40, analyzer);
        writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        try {
            // 生成したインデックスの配置先
            Directory directory = FSDirectory.open(new File(INDEX_DIR));

            // インデックス生成クラス
            IndexWriter indexWriter = new IndexWriter(directory, writerConfig);

            Random random = new Random();

            for (int i = 0; i < INDEX_COUNT; i++) {
                Document document = new Document();

                // フィールド:番号(数値)
                Field fieldIntNum = new IntField("num", i, Field.Store.YES);
                document.add(fieldIntNum);

                // フィールド:番号(文字列)
                Field fieldStrNum = new StringField("str_num", Integer.toString(i), Field.Store.YES);
                document.add(fieldStrNum);

                // フィールド:値
                Field fieldVal = new StringField("val", "value" + random.nextInt(INDEX_COUNT), Field.Store.YES);
                document.add(fieldVal);

                // フィールド:日付
                Field fieldDate = new StringField("date", new Date().toString(), Field.Store.YES);
                document.add(fieldDate);

                // インデックスに追加
                indexWriter.addDocument(document);
            }

            // インデックス生成終了
            indexWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("インデックス生成が完了しました");
    }
}
