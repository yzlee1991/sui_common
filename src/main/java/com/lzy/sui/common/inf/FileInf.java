package com.lzy.sui.common.inf;

import java.util.List;

import com.lzy.sui.common.model.TreeFileList;

public interface FileInf {

	public List<TreeFileList> getFileList();

	public byte[] getFilePart(String filePath, int partSize, int partNum);

	public String show(String a);
	
}
