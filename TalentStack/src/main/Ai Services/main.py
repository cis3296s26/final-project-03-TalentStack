
from flask import Flask, request, jsonify
from flask_cors import CORS
from openai import OpenAI
import os
 
app = Flask(__name__)
CORS(app)  # Allow requests from Spring Boot
 
client = OpenAI(api_key=os.environ.get("OPENAI_API_KEY"))
 
 
@app.route("/api/ai/interview-schedule", methods=["POST"])
def generate_interview_schedule():
    """
    Accepts job/interview details and returns a day-by-day prep schedule.
 
    Expected JSON body:
    {
        "jobTitle": "Software Engineer",
        "company": "Google",
        "interviewDate": "2025-04-20",   // ISO date string
        "skills": ["Java", "System Design", "LeetCode"],
        "experienceLevel": "Mid-level"   // optional
    }
    """
    data = request.get_json()
 
    if not data:
        return jsonify({"error": "Request body is required"}), 400
 
    job_title = data.get("jobTitle", "")
    company = data.get("company", "")
    interview_date = data.get("interviewDate", "")
    skills = data.get("skills", [])
    experience_level = data.get("experienceLevel", "Mid-level")
 
    if not job_title or not interview_date:
        return jsonify({"error": "jobTitle and interviewDate are required"}), 400
 
    skills_str = ", ".join(skills) if skills else "general software engineering topics"
 
    prompt = f"""
You are an expert career coach helping a candidate prepare for a job interview.
 
Create a detailed, day-by-day interview preparation schedule for the following:
- Job Title: {job_title}
- Company: {company if company else "the company"}
- Interview Date: {interview_date}
- Key Skills to Focus On: {skills_str}
- Experience Level: {experience_level}
 
Generate a structured schedule starting from today until the interview date.
For each day provide:
1. A clear focus area/theme for the day
2. 3-4 specific tasks (e.g., study topics, practice problems, mock interviews)
3. Estimated time per task
4. A motivational tip or resource recommendation
 
Format the response as a JSON object with this structure:
{{
  "totalDays": <number>,
  "schedule": [
    {{
      "day": <number>,
      "date": "<YYYY-MM-DD>",
      "theme": "<focus area>",
      "tasks": [
        {{
          "title": "<task name>",
          "description": "<what to do>",
          "duration": "<e.g. 1 hour>",
          "resource": "<optional link or book>"
        }}
      ],
      "tip": "<motivational tip or advice>"
    }}
  ],
  "finalAdvice": "<overall advice for the interview>"
}}
 
Return ONLY valid JSON, no markdown or extra text.
"""
 
    response = client.chat.completions.create(
        model="gpt-4o",
        messages=[
            {
                "role": "system",
                "content": "You are a professional career coach and interview preparation expert. Always respond with valid JSON only.",
            },
            {"role": "user", "content": prompt},
        ],
        temperature=0.7,
        max_tokens=3000,
    )
 
    schedule_json = response.choices[0].message.content
 
    # Parse to validate it's proper JSON before sending back
    import json
    schedule_data = json.loads(schedule_json)
 
    return jsonify(schedule_data), 200
 
 
@app.route("/api/ai/health", methods=["GET"])
def health_check():
    return jsonify({"status": "ok", "service": "TalentStack AI Service"}), 200
 
 
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)