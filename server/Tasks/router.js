import exp from 'express';
import { protect } from '../middlewares/auth.js';
import { IsAuthor, ProjectCheck } from '../middlewares/projects.js';
import { CompleteTask, CreateTask, DeleteTask, ListTask, UpdateTask } from './control.js';

const router = exp.Router();

router.get('/my', protect, ListTask);
router.get('/:project_id', protect, ProjectCheck, ListTask);
router.get('/:project_id/:key/complete', protect, ProjectCheck, CompleteTask);

router.post('/:project_id', protect, ProjectCheck, IsAuthor, CreateTask);
router.put('/:project_id/:key', protect, ProjectCheck, IsAuthor, UpdateTask);
router.delete('/:project_id/:key', protect, ProjectCheck, IsAuthor, DeleteTask);

export default router;
