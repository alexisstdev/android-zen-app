const express = require("express");
const cors = require("cors");

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// In-memory storage
const storage = {
	users: new Map(),
	mindfulPrompts: [
		"¿Para qué necesito abrir esta app ahora?",
		"¿Qué estoy evitando hacer en este momento?",
		"¿Esto me acerca a mis metas del día?",
		"¿Puedo esperar 5 minutos más?",
		"¿Qué actividad más importante podría estar haciendo?",
		"¿Estoy actuando por impulso o por necesidad real?",
		"¿Cómo me sentiré después de usar esta app?",
		"¿Hay algo urgente que requiera mi atención ahora?",
	],
	miniTasks: [
		"Bebe un vaso de agua y estírate",
		"Haz 10 respiraciones profundas",
		"Mira por la ventana durante 30 segundos",
		"Escribe una cosa por la que estás agradecido",
		"Levántate y camina un minuto",
		"Cierra los ojos y relaja los hombros",
		"Organiza tu espacio de trabajo",
		"Lee una página de un libro físico",
		"Llama a alguien que no has visto en un tiempo",
		"Haz un estiramiento de cuello y espalda",
	],
	focusQuotes: [
		"La concentración es el secreto de la fuerza. - Ralph Waldo Emerson",
		"Lo que no se programa, no se hace. - Cal Newport",
		"La multitarea es hacer mal varias cosas a la vez.",
		"Tu atención es tu recurso más valioso.",
		"Las notificaciones pueden esperar, tus metas no.",
		"El enfoque profundo es un superpoder en el siglo XXI.",
		"Menos estímulos, más creatividad.",
		"Tu tiempo es limitado, no lo desperdicies.",
		"La disciplina es elegir entre lo que quieres ahora y lo que quieres más.",
		"El secreto está en hacer menos, pero mejor.",
	],
};

// Helper to get or create user
const getOrCreateUser = (userId) => {
	if (!storage.users.has(userId)) {
		storage.users.set(userId, {
			userId,
			settings: {
				selectedApps: [],
				customMessage:
					"Mantén el enfoque. Esta app estará disponible en un momento.",
				pauseTime: "30 segundos",
				dailyOpens: "3",
				sessionDuration: "10 minutos",
			},
			usageBackups: [],
		});
	}
	return storage.users.get(userId);
};

// ============ SETTINGS ENDPOINTS ============

// Get user settings
app.get("/api/v1/users/:userId/settings", (req, res) => {
	try {
		const { userId } = req.params;
		const user = getOrCreateUser(userId);

		res.json({
			success: true,
			data: user.settings,
		});
	} catch (error) {
		res.status(500).json({
			success: false,
			error: error.message,
		});
	}
});

// Save/Update user settings
app.post("/api/v1/users/:userId/settings", (req, res) => {
	try {
		const { userId } = req.params;
		const settingsData = req.body;

		const user = getOrCreateUser(userId);
		user.settings = {
			...user.settings,
			...settingsData,
		};

		res.json({
			success: true,
			data: user.settings,
			message: "Settings saved successfully",
		});
	} catch (error) {
		res.status(500).json({
			success: false,
			error: error.message,
		});
	}
});

// PUT endpoint (same as POST)
app.put("/api/v1/users/:userId/settings", (req, res) => {
	try {
		const { userId } = req.params;
		const settingsData = req.body;

		const user = getOrCreateUser(userId);
		user.settings = {
			...user.settings,
			...settingsData,
		};

		res.json({
			success: true,
			data: user.settings,
			message: "Settings updated successfully",
		});
	} catch (error) {
		res.status(500).json({
			success: false,
			error: error.message,
		});
	}
});

// ============ USAGE BACKUP ENDPOINT ============

app.post("/api/v1/users/:userId/usage/backup", (req, res) => {
	try {
		const { userId } = req.params;
		const usageData = req.body;

		const user = getOrCreateUser(userId);

		const backup = {
			timestamp: new Date().toISOString(),
			...usageData,
		};

		user.usageBackups.push(backup);

		// Keep only last 30 backups
		if (user.usageBackups.length > 30) {
			user.usageBackups = user.usageBackups.slice(-30);
		}

		res.json({
			success: true,
			data: backup,
			message: "Usage data backed up successfully",
		});
	} catch (error) {
		res.status(500).json({
			success: false,
			error: error.message,
		});
	}
});

// ============ DYNAMIC CONTENT ENDPOINTS ============

// Get random mindful prompt
app.get("/api/v1/mindful_prompts", (req, res) => {
	try {
		const randomPrompt =
			storage.mindfulPrompts[
				Math.floor(Math.random() * storage.mindfulPrompts.length)
			];

		res.json({
			success: true,
			data: {
				prompt: randomPrompt,
			},
		});
	} catch (error) {
		res.status(500).json({
			success: false,
			error: error.message,
		});
	}
});

// Get daily mini task
app.get("/api/v1/mini_tasks/daily", (req, res) => {
	try {
		const randomTask =
			storage.miniTasks[Math.floor(Math.random() * storage.miniTasks.length)];

		res.json({
			success: true,
			data: {
				task: randomTask,
			},
		});
	} catch (error) {
		res.status(500).json({
			success: false,
			error: error.message,
		});
	}
});

// Get focus quote
app.get("/api/v1/quotes/focus", (req, res) => {
	try {
		const randomQuote =
			storage.focusQuotes[
				Math.floor(Math.random() * storage.focusQuotes.length)
			];

		res.json({
			success: true,
			data: {
				quote: randomQuote,
			},
		});
	} catch (error) {
		res.status(500).json({
			success: false,
			error: error.message,
		});
	}
});

// ============ HEALTH CHECK ============

app.get("/api/v1/health", (req, res) => {
	res.json({
		success: true,
		message: "ZenApp API is running",
		timestamp: new Date().toISOString(),
	});
});

// Start server
app.listen(PORT, () => {
	console.log(`🚀 ZenApp API running on http://localhost:${PORT}`);
	console.log(`📊 Health check: http://localhost:${PORT}/api/v1/health`);
});
