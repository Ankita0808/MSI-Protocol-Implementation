public class L1Block {

	String tag;	
// 	String index;
//	String offset;
	String status;
	
	public L1Block()
	{
		this.tag=null;
		this.status = "Invalidate";
	}

	public L1Block(String tag, String status) {
		super();
		this.tag = tag;
		this.status = status;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
