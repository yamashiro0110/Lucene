package test;

import org.apache.commons.io.FileUtils;
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
import java.util.List;
import java.util.Random;

/**
 * インデックス生成のテスト
 * @author yamashiro-r
 */
public class IndexCreate {

    public static void main(String[] args) {
        IndexCreate indexCreate = new IndexCreate();
        indexCreate.createDataFiles();
        indexCreate.createIndex();
    }

    /**
     * インデックス配置先
     */
    private static final String INDEX_DIR = "index";

    /**
     * インデックスに追加するデータファイルディレクトリ
     */
    private static final String INDEX_FILE_DIR = "index_files";

    /**
     * インデックスに追加するデータファイル
     */
    private static final String INDEX_DATA_PATH = INDEX_FILE_DIR + "/index_data.txt";

    /**
     * インデックスに追加するデータの区切り文字
     */
    private static final String DATA_SPLITER = "\t";

    /**
     * インデックスに追加するデータファイルを作成する
     */
    public void createDataFiles() {
        System.out.println("インデックス生成用データファイルの作成を開始します");
        Random random = new Random();

        // データファイル出力用
        StringBuilder rowBuilder = new StringBuilder();

        // 出力する内容を追加
        for (int i = 0; i < 1000; i++) {
            rowBuilder
            .append(i).append(DATA_SPLITER)
            .append("value" + random.nextInt(1000)).append(DATA_SPLITER)
            .append(new Date()).append("\n");
        }

        try {
            // ファイルに出力
            FileUtils.writeStringToFile(new File(INDEX_DATA_PATH), rowBuilder.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("インデックス生成用データファイルの作成が終了しました");
    }

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

            @SuppressWarnings("unchecked")
            List<String> lineList = FileUtils.readLines(new File(INDEX_DATA_PATH));

            for (String line : lineList) {
                // 読み込んだ行を分割
                String[] datas = line.split(DATA_SPLITER);

                // インデックスに追加するオブジェクト
                Document document = new Document();

                // フィールド:番号(数値)
                Field fieldIntNum = new IntField("num", Integer.parseInt(datas[0]), Field.Store.YES);
                document.add(fieldIntNum);

                // フィールド:番号(文字列)
                Field fieldStrNum = new StringField("str_num", datas[0], Field.Store.YES);
                document.add(fieldStrNum);

                // フィールド:値
                Field fieldVal = new StringField("val", datas[1], Field.Store.YES);
                document.add(fieldVal);

                // フィールド:日付
                Field fieldDate = new StringField("date", datas[2], Field.Store.YES);
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
