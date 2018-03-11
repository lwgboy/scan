package xxtt.scan.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FileTools {
    /**
     * 文件名字排序（按ASC码排序)
     * 
     * @param file
     * @return
     */
    public static File[] getFilsOrder(File file) {
	File[] files = null;
	if (file.exists()) {
	    files = file.listFiles();
	    File temp;
	    if (files != null && files.length > 0) {
		for (int i = 0; i < files.length; i++) {
		    for (int j = 0; j < files.length - i - 1; j++) {
			if (files[j].getName()
				.compareTo(files[j + 1].getName()) > 0) {
			    temp = files[j];
			    files[j] = files[j + 1];
			    files[j + 1] = temp;
			}
		    }
		}
	    }
	}
	return files;
    }

    /**
     * 获取path路径下的文件列表
     * 
     * @param path
     * @return
     */
    public static List<Map<String, Object>> getFileDir(String path) {
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	// 获取文件夹目录数组
	File pfile = new File(path);
	File[] files = FileTools.getFilsOrder(pfile);
	if (pfile.getParent() != null) {
	    Map<String, Object> root = new HashMap<String, Object>();
	    // root.put("img", R.drawable.lastdir);
	    root.put("name", "返回上级目录");
	    root.put("path", pfile.getParent());
	    list.add(root);
	}
	if (files != null && files.length > 0) {
	    for (File file : files) {
		Map<String, Object> folder = new HashMap<String, Object>();
		if (file.isDirectory()) {
		    // 获取文件夹目录结构
		    // folder.put("img", R.drawable.folder);
		    folder.put("name", file.getName());
		    folder.put("path", file.getPath());
		    list.add(folder);
		} else {
		    String fileName = file.getName();
		    String res = "(.*\\.xls)|(.*\\.XLS)";
		    if (Pattern.matches(res, fileName)) {
			// folder.put("img", R.drawable.excel_pic);
		    } else {
			// folder.put("img", R.drawable.mexplorer);
		    }
		    folder.put("name", file.getName());
		    folder.put("path", file.getPath());
		    list.add(folder);
		}
	    }
	}
	files = null;
	return list;
    }
}
