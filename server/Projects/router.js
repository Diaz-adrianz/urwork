import exp from 'express';
import { protect } from '../middlewares/auth.js';
import { IsAuthor, ProjectCheck } from '../middlewares/projects.js';
import { CreateProj, DeleteProj, DetailProj, ListProj, StarringProj, UpdateProj } from './control.js';

const router = exp.Router();

router.get('/', protect, ListProj);
router.get('/my', protect, ListProj);

router.get('/starit/:key/:option', protect, StarringProj);
router.get('/:key', protect, DetailProj);

router.post('/', protect, CreateProj);
router.put('/:key', protect, ProjectCheck, IsAuthor, UpdateProj);
router.delete('/:key', protect, ProjectCheck, IsAuthor, DeleteProj);

export default router;
