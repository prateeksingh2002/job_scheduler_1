// "use client";
//
// import { useState } from "react";
// import axios from "axios";
//
// export default function JobScheduler() {
//   const [formType, setFormType] = useState("IMMEDIATE");
//   const [formData, setFormData] = useState({
//     jobName: "",
//     jarFileName: "",
//     jobType: "IMMEDIATE",
//     scheduledTime: "",
//     cronExpression: "",
//     timeZone: "UTC",
//     topic: "",
//     message: "",
//     delayMinutes: 1,
//   });
//   const [response, setResponse] = useState<string | object | null>(null);
//
//   const backendBaseUrl = process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080";
//
//   const handleChange = (
//     e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
//   ) => {
//     const { name, value } = e.target;
//     setFormData((prev) => ({ ...prev, [name]: value }));
//   };
//
//   const handleJobSubmit = async () => {
//     try {
//       const payload: {
//         jobName: string;
//         jarFileName: string;
//         jobType: string;
//         timeZone: string;
//         scheduledTime?: string;
//         cronExpression?: string;
//       } = {
//         jobName: formData.jobName,
//         jarFileName: formData.jarFileName,
//         jobType: formData.jobType,
//         timeZone: formData.timeZone,
//       };
//
//       if (formType === "SCHEDULED") {
//         payload.scheduledTime = formData.scheduledTime;
//       } else if (formType === "RECURRING") {
//         payload.cronExpression = formData.cronExpression;
//       }
//
//       const res = await axios.post(`${backendBaseUrl}/api/jobs`, payload);
//       setResponse(res.data);
//     } catch (err) {
//       console.error(err);
//       setResponse("Error occurred while scheduling job.");
//     }
//   };
//
//   const handleMessageSubmit = async () => {
//     try {
//       const res = await axios.post(`${backendBaseUrl}/api/messages/delay`, {
//         topic: formData.topic,
//         message: formData.message,
//         delayMinutes: formData.delayMinutes,
//       });
//       setResponse(res.data);
//     } catch (err) {
//       console.error(err);
//       setResponse("Error occurred while sending message.");
//     }
//   };
//
//   return (
//     <div className="p-6 max-w-3xl mx-auto">
//       <h1 className="text-2xl font-bold mb-4">Job Scheduler</h1>
//
//       <div className="mb-4">
//         <label className="block font-medium">Select Action:</label>
//         <select
//           name="formType"
//           value={formType}
//           onChange={(e) => {
//             const selected = e.target.value;
//             setFormType(selected);
//             setFormData((prev) => ({ ...prev, jobType: selected }));
//           }}
//           className="border p-2 w-full rounded"
//         >
//           <option value="IMMEDIATE">Immediate Job</option>
//           <option value="SCHEDULED">Scheduled Job</option>
//           <option value="RECURRING">Recurring Job</option>
//           <option value="MESSAGE">Send Delayed Message</option>
//         </select>
//       </div>
//
//       {formType !== "MESSAGE" && (
//         <>
//           <input
//             className="border p-2 mb-2 w-full rounded"
//             type="text"
//             name="jobName"
//             placeholder="Job Name"
//             value={formData.jobName}
//             onChange={handleChange}
//           />
//           <input
//             className="border p-2 mb-2 w-full rounded"
//             type="text"
//             name="jarFileName"
//             placeholder="JAR File Name"
//             value={formData.jarFileName}
//             onChange={handleChange}
//           />
//         </>
//       )}
//
//       {formType === "SCHEDULED" && (
//         <input
//           className="border p-2 mb-2 w-full rounded"
//           type="datetime-local"
//           name="scheduledTime"
//           value={formData.scheduledTime}
//           onChange={handleChange}
//         />
//       )}
//
//       {formType === "RECURRING" && (
//         <input
//           className="border p-2 mb-2 w-full rounded"
//           type="text"
//           name="cronExpression"
//           placeholder="Cron Expression"
//           value={formData.cronExpression}
//           onChange={handleChange}
//         />
//       )}
//
//       {formType !== "MESSAGE" && (
//         <input
//           className="border p-2 mb-4 w-full rounded"
//           type="text"
//           name="timeZone"
//           placeholder="Time Zone"
//           value={formData.timeZone}
//           onChange={handleChange}
//         />
//       )}
//
//       {formType === "MESSAGE" && (
//         <>
//           <input
//             className="border p-2 mb-2 w-full rounded"
//             type="text"
//             name="topic"
//             placeholder="Kafka Topic"
//             value={formData.topic}
//             onChange={handleChange}
//           />
//           <textarea
//             className="border p-2 mb-2 w-full rounded"
//             name="message"
//             placeholder="Message"
//             value={formData.message}
//             onChange={handleChange}
//           />
//           <input
//             className="border p-2 mb-4 w-full rounded"
//             type="number"
//             name="delayMinutes"
//             placeholder="Delay in Minutes"
//             value={formData.delayMinutes}
//             onChange={handleChange}
//           />
//         </>
//       )}
//
//       <button
//         className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
//         onClick={formType === "MESSAGE" ? handleMessageSubmit : handleJobSubmit}
//       >
//         Submit
//       </button>
//
//       {response && (
//         <div className="mt-4 p-4 bg-gray-100 rounded border">
//           <pre>{JSON.stringify(response, null, 2)}</pre>
//         </div>
//       )}
//     </div>
//   );
// }
//



"use client";

import { useEffect, useState } from "react";
import axios from "axios";

const timeZones = Intl.supportedValuesOf("timeZone");

export default function JobScheduler() {
  const [formType, setFormType] = useState("IMMEDIATE");
  const [jarFiles, setJarFiles] = useState<string[]>([]);
  const [formData, setFormData] = useState({
    jobName: "",
    jarFileName: "",
    jobType: "IMMEDIATE",
    scheduledTime: "",
    cronExpression: "",
    timeZone: "UTC",
    topic: "",
    message: "",
    delayMinutes: 1,
  });
  const [response, setResponse] = useState<string | object | null>(null);

  const backendBaseUrl = process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080";

  useEffect(() => {
    axios.get(`${backendBaseUrl}/api/jars`).then((res) => setJarFiles(res.data));
  }, []);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleJobSubmit = async () => {
    try {
      const payload: {
        jobName: string;
        jarFileName: string;
        jobType: string;
        timeZone: string;
        scheduledTime?: string;
        cronExpression?: string;
      } = {
        jobName: formData.jobName,
        jarFileName: formData.jarFileName,
        jobType: formData.jobType,
        timeZone: formData.timeZone,
      };

      if (formType === "SCHEDULED") {
        payload.scheduledTime = formData.scheduledTime;
      } else if (formType === "RECURRING") {
        payload.cronExpression = formData.cronExpression;
      }

      const res = await axios.post(`${backendBaseUrl}/api/jobs`, payload);
      setResponse(res.data);
    } catch (err) {
      console.error(err);
      setResponse("Error occurred while scheduling job.");
    }
  };

  const handleMessageSubmit = async () => {
    try {
      const res = await axios.post(`${backendBaseUrl}/api/messages/delay`, {
        topic: formData.topic,
        message: formData.message,
        delayMinutes: formData.delayMinutes,
      });
      setResponse(res.data);
    } catch (err) {
      console.error(err);
      setResponse("Error occurred while sending message.");
    }
  };

  return (
    <div className="p-6 max-w-3xl mx-auto">
      <h1 className="text-2xl font-bold mb-4">Job Scheduler</h1>

      <div className="mb-4">
        <label className="block font-medium">Select Action:</label>
        <select
          name="formType"
          value={formType}
          onChange={(e) => {
            const selected = e.target.value;
            setFormType(selected);
            setFormData((prev) => ({ ...prev, jobType: selected }));
          }}
          className="border p-2 w-full rounded"
        >
          <option value="IMMEDIATE">Immediate Job</option>
          <option value="SCHEDULED">Scheduled Job</option>
          <option value="RECURRING">Recurring Job</option>
          <option value="MESSAGE">Send Delayed Message</option>
        </select>
      </div>

      {formType !== "MESSAGE" && (
        <>
          <input
            className="border p-2 mb-2 w-full rounded"
            type="text"
            name="jobName"
            placeholder="Job Name"
            value={formData.jobName}
            onChange={handleChange}
          />
          <select
            className="border p-2 mb-2 w-full rounded"
            name="jarFileName"
            value={formData.jarFileName}
            onChange={handleChange}
          >
            <option value="">Select JAR</option>
            {jarFiles.map((jar) => (
              <option key={jar} value={jar}>{jar}</option>
            ))}
          </select>
        </>
      )}

      {formType === "SCHEDULED" && (
        <input
          className="border p-2 mb-2 w-full rounded"
          type="datetime-local"
          name="scheduledTime"
          value={formData.scheduledTime}
          onChange={handleChange}
        />
      )}

      {formType === "RECURRING" && (
        <select
          className="border p-2 mb-2 w-full rounded"
          name="cronExpression"
          value={formData.cronExpression}
          onChange={handleChange}
        >
          <option value="">Select Frequency</option>
          <option value="0 0 * * *">Daily</option>
          <option value="0 * * * *">Hourly</option>
          <option value="0 0 * * 0">Weekly</option>
          <option value="0 0 1 * *">Monthly</option>
        </select>
      )}

      {formType !== "MESSAGE" && (
        <select
          className="border p-2 mb-4 w-full rounded"
          name="timeZone"
          value={formData.timeZone}
          onChange={handleChange}
        >
          {timeZones.map((tz) => (
            <option key={tz} value={tz}>{tz}</option>
          ))}
        </select>
      )}

      {formType === "MESSAGE" && (
        <>
          <input
            className="border p-2 mb-2 w-full rounded"
            type="text"
            name="topic"
            placeholder="Kafka Topic"
            value={formData.topic}
            onChange={handleChange}
          />
          <textarea
            className="border p-2 mb-2 w-full rounded"
            name="message"
            placeholder="Message"
            value={formData.message}
            onChange={handleChange}
          />
          <input
            className="border p-2 mb-4 w-full rounded"
            type="number"
            name="delayMinutes"
            placeholder="Delay in Minutes"
            value={formData.delayMinutes}
            onChange={handleChange}
          />
        </>
      )}

      <button
        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        onClick={formType === "MESSAGE" ? handleMessageSubmit : handleJobSubmit}
      >
        Submit
      </button>

      {response && (
        <div className="mt-4 p-4 bg-gray-100 rounded border">
          <pre>{JSON.stringify(response, null, 2)}</pre>
        </div>
      )}
    </div>
  );
}
