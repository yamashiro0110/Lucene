package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

/**
 * インデックス更新のテスト
 * @author yamashiro-r
 */
public class IndexUpdate {
    public static void main(String[] args) {
        try {
            IndexCreate indexCreate = new IndexCreate();
            IndexSearch indexSearch = new IndexSearch();

            // 標準入力から値を読込
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;

            while (true) {
                System.out.println("***************************");
                line = reader.readLine();

                if (line.equals("")) {
                    System.out.println("終了");
                    break;
                }
                if (line.equals("search")) {
                    indexSearch.wildCardQuery("val", "value11*");
                }
                else if (line.equals("create")) {
                    indexCreate.createDataFiles();
                    indexCreate.createIndex();
                }
                else if (line.equals("refresh")) {
                    indexSearch.maybeRefresh();
                }
                else if (line.equals("refresh_aquire")) {
                    indexSearch.maybeRefreshAndAquire();
                }
                else if (line.equals("refresh_blocking")) {
                    indexSearch.maybeRefreshBlocking();
                }
                else {
                    System.out.println("入力値が一致しません:" + line);
                }
            }

            IOUtils.closeQuietly(reader);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
