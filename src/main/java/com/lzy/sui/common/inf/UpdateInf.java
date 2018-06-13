package com.lzy.sui.common.inf;

public interface UpdateInf extends Rmiable{

	public Long getCorpseLastestSize();
	
	public byte[] getCorpseUpdatePart(int partSize, int partNum);
	
}
