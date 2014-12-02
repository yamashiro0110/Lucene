package test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * インデックス検索のテスト
 * @author yamashiro-r
 */
public class IndexSearch {
    public static void main(String[] main) {
        try {
            IndexSearch indexSearch = new IndexSearch();
            indexSearch.searchIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * インデックス配置先
     */
    private static final String INDEX_DIR = "index";

    private IndexReader indexReader;
    private IndexSearcher indexSearcher;

    /**
     * コンストラクタ
     * @throws IOException
     */
    public IndexSearch() throws IOException {
        indexReader = DirectoryReader.open(FSDirectory.open(new File(INDEX_DIR)));
        indexSearcher = new IndexSearcher(indexReader);
    }

    /**
     * インデックス検索を行う
     */
    public void searchIndex() {
        System.out.println("*******************************");
        System.out.println("standardQuery開始");
        standardQuery("str_num", "16");
        standardQuery("val", "value840");

        System.out.println("*******************************");
        System.out.println("numericRangeQuery開始");
        numericRangeQuery(10);

        System.out.println("*******************************");
        System.out.println("wildCardQuery開始");
        wildCardQuery("str_num", "1*");
        wildCardQuery("val", "value9*");

        System.out.println("*******************************");
        System.out.println("termQuery開始");
        termQuery("str_num", "100");
        termQuery("val", "value99");

        System.out.println("*******************************");
        System.out.println("matchAllDocsQuery開始");
        matchAllDocsQuery();
    }

    private void standardQuery(String fieldName, String searchValue) {
        try {
            System.out.println();
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
            QueryParser queryParser = new QueryParser(Version.LUCENE_40, fieldName, analyzer);
            Query query = queryParser.parse(searchValue);
            System.out.println("query -> " + query.toString());
            TopDocs result = indexSearcher.search(query, 3);
            showSearchResult(result);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    private void numericRangeQuery(int num) {
        try {
            System.out.println();
            Query query = NumericRangeQuery.newIntRange("num", num, num, true, true);
            System.out.println("query -> " + query.toString());
            TopDocs result = indexSearcher.search(query, 3);
            showSearchResult(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void wildCardQuery(String fieldName, String searchValue) {
        try {
            System.out.println();
            WildcardQuery query = new WildcardQuery(new Term(fieldName, searchValue));
            System.out.println("query -> " + query.toString());
            TopDocs result = indexSearcher.search(query, 3);
            showSearchResult(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void termQuery(String fieldName, String searchValue) {
        try {
            System.out.println();
            TermQuery query = new TermQuery(new Term(fieldName, searchValue));
            System.out.println("query -> " + query.toString());
            TopDocs result = indexSearcher.search(query, 3);
            showSearchResult(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void matchAllDocsQuery() {
        try {
            System.out.println();
            MatchAllDocsQuery query = new MatchAllDocsQuery();
            System.out.println("query -> " + query.toString());
            TopDocs result = indexSearcher.search(query, 3);
            showSearchResult(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 検索結果を表示
     * @param result
     */
    private void showSearchResult(TopDocs result) {
        System.out.println("検索結果の表示開始");
        ScoreDoc[] hits = result.scoreDocs;
        System.out.println(String.format("検索結果件数 hits.length:%d, result.totalHits:%s",hits.length, result.totalHits));

        for (ScoreDoc scoreDoc : hits) {
            try {
                Document document = indexSearcher.doc(scoreDoc.doc);

                System.out.println(String.format(
                        "doc:%d, num:%s, val:%s, date:%s",
                        scoreDoc.doc,
                        document.get("num"),
                        document.get("val"),
                        document.get("date")
                ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
