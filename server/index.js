import exp from 'express';
import dotenv from 'dotenv';
import cookieParser from 'cookie-parser';
import bodyParser from 'body-parser';
import cors from 'cors';

import connDB from './db.js';
import rAuth from './Auth/router.js';
import rUsers from './Users/router.js';
import rProj from './Projects/router.js';
import rTasks from './Tasks/router.js';

dotenv.config();

const app = exp();
const port = process.env.PORT || 5000;

app.use(cookieParser());
app.use(cors({ credentials: true, origin: 'http://localhost:3000' }));
app.use(exp.json());
app.use(
	exp.urlencoded({
		extended: true,
	})
);
connDB();

// ROUTES
app.use('/auth', rAuth);
app.use('/users', rUsers);
app.use('/projects', rProj);
app.use('/tasks', rTasks);
app.use('*', (req, res) => {
	return res.status(404).json({
		msg: 'API path not found',
	});
});

app.listen(port, () => {
	console.log(`URWORK | listening on port ${port}`);
});
