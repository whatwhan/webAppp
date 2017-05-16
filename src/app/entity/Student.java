package app.entity;

public class Student {

	private String xuehao;
	private String xingming;
	private String banji;
	private String zhuanye;
	private String xueyuan;
	public String getXuehao() {
		return xuehao;
	}
	public void setXuehao(String xuehao) {
		this.xuehao = xuehao;
	}
	public String getXingming() {
		return xingming;
	}
	public void setXingming(String xingming) {
		this.xingming = xingming;
	}
	public String getBanji() {
		return banji;
	}
	public void setBanji(String banji) {
		this.banji = banji;
	}
	public String getZhuanye() {
		return zhuanye;
	}
	public void setZhuanye(String zhuanye) {
		this.zhuanye = zhuanye;
	}
	public String getXueyuan() {
		return xueyuan;
	}
	public void setXueyuan(String xueyuan) {
		this.xueyuan = xueyuan;
	}
	public Student(String xuehao, String xingming, String banji,
			String zhuanye, String xueyuan) {
		super();
		this.xuehao = xuehao;
		this.xingming = xingming;
		this.banji = banji;
		this.zhuanye = zhuanye;
		this.xueyuan = xueyuan;
	}
	@Override
	public String toString() {
		return "Student [xuehao=" + xuehao + ", xingming=" + xingming
				+ ", banji=" + banji + ", zhuanye=" + zhuanye + ", xueyuan="
				+ xueyuan + "]";
	}
	
	
	
}
