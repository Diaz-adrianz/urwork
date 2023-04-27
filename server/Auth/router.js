import exp from 'express';
import { Gauth, Login, Logout, Register } from './control.js';

const router = exp.Router();

router.post('/gauth', Gauth);
router.post('/signup', Register);
router.post('/signin', Login);
router.delete('/signout', Logout);

export default router;
