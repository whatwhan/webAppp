package app.entity;

public class ScoreRecord {
	public String lessonCode = ""; // 课程代码
	public String lessonName = ""; // 课程名称
	public String category = ""; // 类别
	public String lessonScore = ""; // 学分
	public String score = ""; // 成绩
	public String remark = ""; // 备注

	@Override
	public String toString() {
		return lessonName + ":" + score + "\n";
	}

	public String getLessonCode() {
		return lessonCode;
	}

	public void setLessonCode(String lessonCode) {
		this.lessonCode = lessonCode;
	}

	public String getLessonName() {
		return lessonName;
	}

	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLessonScore() {
		return lessonScore;
	}

	public void setLessonScore(String lessonScore) {
		this.lessonScore = lessonScore;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
