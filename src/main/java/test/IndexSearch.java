package test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
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

    /**
     * IndexSeacher管理クラス
     */
    private SearcherManager searcherManager;

    /**
     * インデックス検索を実行するクラス
     */
    private IndexSearcher indexSearcher;

    /**
     * コンストラクタ
     * @throws IOException
     */
    public IndexSearch() throws IOException {
        Directory dir = FSDirectory.open(new File(INDEX_DIR));
        searcherManager = new SearcherManager(dir, new SearcherFactory());
        indexSearcher = searcherManager.acquire();
    }

    /**
     * インデックスが更新されていれば再読み込み
     * @return
     * @throws IOException
     */
    public boolean maybeRefresh() throws IOException {
        System.out.println("インデックス再読み込みを実行します:maybyRefresh");
        if (searcherManager.maybeRefresh()) {
            System.out.println("インデックス再読み込みが完了しました:maybyRefresh");
            return true;
        }
        else {
            System.out.println("インデックス再読み込みに失敗しました:maybyRefresh");
            return false;
        }
    }

    /**
     * インデックスが更新されていれば再読み込み
     * indexSearcherのインスタンスも更新する
     * @return
     * @throws IOException
     */
    public boolean maybeRefreshAndAquire() throws IOException {
        if (searcherManager.maybeRefresh()) {
            System.out.println("インデックス再読み込みが完了しました:maybeRefreshAndAquire");
            indexSearcher = searcherManager.acquire();
            return true;
        }
        else {
            System.out.println("インデックス再読み込みに失敗しました:maybeRefreshAndAquire");
            return false;
        }
    }

    /**
     * インデックスが更新されていれば再読み込み
     * 別スレッドによりmaybeRefreshが実行されている場合は待つ
     * @throws IOException
     */
    public void maybeRefreshBlocking() throws IOException {
        System.out.println("インデックス再読み込みを実行します:maybeRefreshBlocking");
        searcherManager.maybeRefreshBlocking();
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
        numericRangeQuery(10, 12, true, true);
        numericRangeQuery(10, 12, true, false);
        numericRangeQuery(10, 12, false, true);
        numericRangeQuery(10, 12, false, false);

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

        System.out.println("*******************************");
        System.out.println("booleanQuery開始");
        booleanQuery(BooleanClause.Occur.MUST);
    }

    public void standardQuery(String fieldName, String searchValue) {
        try {
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

    public void numericRangeQuery(int min, int max, boolean minInclusive, boolean maxInclusive) {
        try {
            Query query = NumericRangeQuery.newIntRange("num", min, max, minInclusive, maxInclusive);
            System.out.println(String.format("query -> %s, minInclusive:%b, maxInclusive:%b", query.toString(), minInclusive, maxInclusive));
            TopDocs result = indexSearcher.search(query, 3);
            showSearchResult(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void wildCardQuery(String fieldName, String searchValue) {
        try {
            WildcardQuery query = new WildcardQuery(new Term(fieldName, searchValue));
            System.out.println("query -> " + query.toString());
            TopDocs result = indexSearcher.search(query, 3);
            showSearchResult(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void termQuery(String fieldName, String searchValue) {
        try {
            TermQuery query = new TermQuery(new Term(fieldName, searchValue));
            System.out.println("query -> " + query.toString());
            TopDocs result = indexSearcher.search(query, 3);
            showSearchResult(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void matchAllDocsQuery() {
        try {
            MatchAllDocsQuery query = new MatchAllDocsQuery();
            System.out.println("query -> " + query.toString());
            TopDocs result = indexSearcher.search(query, 3);
            showSearchResult(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void booleanQuery(BooleanClause.Occur occur) {
        try {
            System.out.println();
            Query q1 = new WildcardQuery(new Term("str_num", "10*"));
            Query q2 = new WildcardQuery(new Term("val", "value40*"));

            BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add(q1, occur);
            booleanQuery.add(q2, occur);
            System.out.println("query -> " + booleanQuery.toString());

            TopDocs result = indexSearcher.search(booleanQuery, 3);
            showSearchResult(result);
        } catch (Exception e) {
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
