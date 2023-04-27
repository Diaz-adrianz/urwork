import jwt from 'jsonwebtoken';

export const protect = (req, res, next) => {
	const token = req.cookies?.token || req.headers?.token;

	if (!token) {
		return res.status(403).json({
			msg: 'Login required',
		});
	}

	try {
		const decoded = jwt.verify(token, process.env.JWT_SECRET_KEY);
		req.user = decoded;

		next();
	} catch (error) {
		return res.status(401).json({
			msg: 'Invalid access',
		});
	}
};
